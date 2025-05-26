import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    scenarios: {
        random_load: {
            executor: 'per-vu-iterations',
            vus: 10000,
            iterations: 1,
            maxDuration: '5m',
            exec: 'main',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
};

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomEmail(index) {
    return `rider${index}@email.com`;
}

function randomPassword() {
    const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%';
    return Array.from({ length: 10 }, () => chars.charAt(randomInt(0, chars.length - 1))).join('');
}

function randomAddress() {
    const streets = ['Main St', 'Maple Ave', 'Oak Dr', 'Pine Rd'];
    const cities = ['Springfield', 'Fairview', 'Riverside', 'Greenville'];
    return `${randomInt(100, 9999)} ${streets[randomInt(0, streets.length - 1)]}, ${cities[randomInt(0, cities.length - 1)]}`;
}

export function main() {
    const baseUrl = 'http://localhost:8080/riderapp';

    const email = randomEmail(randomInt(1, 100000));
    const password = randomPassword();
    const phoneNumber = `98765${Math.floor(Math.random() * 10000).toString().padStart(4, '0')}`;
    const address = randomAddress();
    let x = Math.floor(Math.random() * 101);
    let y = Math.floor(Math.random() * 101);

    sleep(Math.random() * 60 * 5);

    // Register Rider
    let res = http.post("http://localhost:8080/riderapp/auth/register", JSON.stringify({
                      email,
                      phoneNumber,
                      password,
                      address,
                      role: "RIDER",
                      x_coordinate: x,
                      y_coordinate: y
                  }), {
                      headers: { 'Content-Type': 'application/json' }
                  });

    check(res, {'Rider registered (201) or email already exists (200)': (r) => r.status === 201 || r.status === 200,});

    if (res.status === 200) {
        return;
    }

    sleep(1);

    // Login to get JWT
    res = http.post(`${baseUrl}/auth/login`, JSON.stringify({
        email,
        password
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    check(res, { 'Logged in successfully': (r) => r.status === 200 });

    const token = res.json('token');
    if (!token) {
        console.error('Login failed, no token returned');
        return;
    }

    const authHeaders = {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    };

    sleep(1);

    res = http.get(`${baseUrl}/ride/rider/match`, authHeaders);
    check(res, { 'Matched rider': (r) => r.status === 200 });

    sleep(1);

    x = Math.floor(Math.random() * 101);
    y = Math.floor(Math.random() * 101);

    res = http.post(`${baseUrl}/ride/start?N=1&destination=Mall&x=${x}&y=${y}`, null, authHeaders);
    check(res, { 'Ride started': (r) => r.status === 200 });

    const rideID = res.json('rideID');
    if (!rideID) {
        console.log('Ride cannot start:', res.json('error'));
        return;
    }

    sleep(1);

    const rideDuration = Math.floor(Math.random() * (100 - 20 + 1)) + 20;
    res = http.post(`${baseUrl}/ride/stop?rideID=${rideID}&timeInMins=${rideDuration}`, null, authHeaders);
    check(res, { 'Stopped ride': (r) => r.status === 200 });

    sleep(1);

    res = http.get(`${baseUrl}/ride/bill?rideID=${rideID}`, authHeaders);
    check(res, { 'Generated bill': (r) => r.status === 200 });

    sleep(1);

    const paymentMethods = ["CASH", "CARD", "WALLET", "UPI"];
    let method = paymentMethods[Math.floor(Math.random() * 4)];

    if (method === "WALLET") {
        res = http.post(`${baseUrl}/payment/add-money?amount=3000&type=UPI`, null, authHeaders);
        check(res, { 'Wallet credited': (r) => r.status === 200 });

        res = http.get(`${baseUrl}/payment/wallet/transactions`, authHeaders);
        check(res, { 'Fetched wallet transactions': (r) => r.status === 200 });
    }

    res = http.post(`${baseUrl}/payment/pay?rideID=${rideID}&type=${method}`, null, authHeaders);
    check(res, { 'Payment successful': (r) => r.status === 200 });

    sleep(1);
}
