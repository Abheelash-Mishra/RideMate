import http from 'k6/http';
import { sleep, check, fail } from 'k6';

// --- Configuration ---
const ADMIN_EMAIL = 'admin@gmail.com';
const ADMIN_PASSWORD = 'admin@test';
const BASE_URL = 'http://localhost:8080/riderapp';
const LOGIN_ENDPOINT = `${BASE_URL}/auth/login`;

const ADMIN_ACTIONS_ITERATIONS_PER_VU = 1;
const NUMBER_OF_ADMIN_VUS = 500;

// --- k6 Options ---
export let options = {
    scenarios: {
        admin_workflow: {
            executor: 'per-vu-iterations',
            vus: NUMBER_OF_ADMIN_VUS,
            iterations: ADMIN_ACTIONS_ITERATIONS_PER_VU,
            maxDuration: '1m',
            exec: 'performAdminActions',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        'http_req_duration{scenario:admin_workflow}': ['p(95)<1000'],
    },
};

export function setup() {
    console.log(`[SETUP] Logging in as admin: ${ADMIN_EMAIL}...`);
    const loginPayload = JSON.stringify({
        email: ADMIN_EMAIL,
        password: ADMIN_PASSWORD,
    });
    const loginParams = {
        headers: { 'Content-Type': 'application/json' },
    };

    const loginRes = http.post(LOGIN_ENDPOINT, loginPayload, loginParams);

    if (loginRes.status !== 200) {
        console.error(`[SETUP] Admin login failed! Status: ${loginRes.status}, Body: ${loginRes.body}`);
        fail(`Admin login failed with status ${loginRes.status}. Cannot proceed with the test.`);
    }

    let adminToken;
    try {
        const responseBody = JSON.parse(loginRes.body);
        adminToken = responseBody.token;
    }
    catch (e) {
        console.error(`[SETUP] Failed to parse admin login response body: ${loginRes.body} - Error: ${e}`);
        fail('Failed to parse admin token from login response.');
    }

    if (!adminToken) {
        console.error(`[SETUP] Admin login succeeded but no token found in response: ${loginRes.body}`);
        fail('Admin token not found after successful login.');
    }

    console.log('[SETUP] Admin logged in successfully. Token obtained.');
    return { adminToken: adminToken };
}

export function performAdminActions(data) {
    const adminToken = data.adminToken;
    const authHeaders = {
        'Authorization': `Bearer ${adminToken}`,
        'Content-Type': 'application/json',
    };

    sleep(Math.random() * 60 * 1);

    let num = Math.floor(Math.random() * (1000 - 200 + 1)) + 200;
    let getDriversRes = http.get(`${BASE_URL}/admin/drivers/list?N=${num}`, { headers: authHeaders });
    check(getDriversRes, {
        'Admin: Fetched drivers successfully (status 200)': (r) => r.status === 200,
    });

    let driverIDs = [];
    if (getDriversRes.status === 200 && getDriversRes.body) {
        try {
            const drivers = JSON.parse(getDriversRes.body);
            if (Array.isArray(drivers)) {
                driverIDs = drivers.map(driver => driver.driverID).filter(id => id != null);
            }
        }
        catch (e) {
            console.error(`VU ${__VU} Iteration ${__ITER}: Error parsing drivers list response: ${e}. Status: ${getDriversRes.status}, Body: ${getDriversRes.body}`);
        }
    }

    if (driverIDs.length === 0) {
        console.log(`VU ${__VU} Iteration ${__ITER}: No driver IDs fetched or list was empty. Skipping further driver-specific actions.`);
        return;
    }

    sleep(Math.random() * 2 + 0.5);

    let driverIDToQuery = driverIDs[Math.floor(Math.random() * driverIDs.length)];
    let getEarningsRes = http.get(`${BASE_URL}/admin/drivers/earnings?driverID=${driverIDToQuery}`, { headers: authHeaders });
    check(getEarningsRes, {
        "Admin: Fetched driver's earnings successfully or attempt acknowledged (status 200 or 404)": (r) => r.status === 200 || r.status === 404,
    });

    sleep(Math.random() * 2 + 0.5);
    if (Math.random() >= 0.9) { // 10% chance to attempt removal
        let driverIDToRemove = driverIDs[Math.floor(Math.random() * driverIDs.length)];

        let removeDriverRes = http.del(`${BASE_URL}/admin/drivers/remove?driverID=${driverIDToRemove}`, null, { headers: authHeaders });
        check(removeDriverRes, {
            'Admin: Removed driver or attempt acknowledged (status 200 or 404)': (r) => r.status === 200 || r.status === 404,
        });
    }
}