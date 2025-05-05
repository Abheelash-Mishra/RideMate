import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    scenarios: {
        random_load: {
            executor: 'per-vu-iterations',
            vus: 500,
            iterations: 1,
            maxDuration: '30s',
            exec: 'main',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
};

export function main() {
    const baseUrl = 'http://localhost:8080/riderapp';

    sleep(Math.random() * 60 * 0.5); // Each VU starts at a random point in the x-minute window

    let num = Math.floor(Math.random() * (1000 - 200 + 1)) + 200;  // To return 200-1000 drivers from the DB
    let res = http.get(`${baseUrl}/admin/drivers/list?N=${num}`);
    let drivers = JSON.parse(res.body);
    let driverIDs = drivers.map(driver => driver.driverID);
    check(res, { 'Fetched drivers': (r) => r.status === 200 });

    let driverID = driverIDs[Math.floor(Math.random() * driverIDs.length)];
    res = http.get(`${baseUrl}/admin/drivers/earnings?driverID=${driverID}`);
    check(res, { "Fetched driver's earnings": (r) => r.status === 200 });

    // Make it so drivers are removed by only 10% of the admin
    if (Math.random() >= 0.9) {
        driverID = driverIDs[Math.floor(Math.random() * driverIDs.length)];
        res = http.del(`${baseUrl}/admin/drivers/remove?driverID=${driverID}`);
        check(res, { 'Removed driver': (r) => r.status === 200 });
    }
}
