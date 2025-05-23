import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    scenarios: {
        random_load: {
            executor: 'per-vu-iterations',
            vus: 10000,
            iterations: 1,
            maxDuration: '2m',
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

function randomPhone() {
    return '9' + Math.floor(100000000 + Math.random() * 900000000);
}

function randomEmail(index) {
    return `driver${index}@email.com`;
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
    const x = randomInt(0, 100);
    const y = randomInt(0, 100);
    const phoneNumber = randomPhone();
    const email = randomEmail(randomInt(1, 999999));
    const password = randomPassword();
    const address = randomAddress();

    sleep(Math.random() * 60 * 2); // Each VU starts at a random point in the x-minute window

    let res = http.post("http://localhost:8080/riderapp/auth/register", JSON.stringify({
        email,
        phoneNumber,
        password,
        address,
        role: "DRIVER",
        x_coordinate: x,
        y_coordinate: y
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    const isSuccessful = check(res, {
        'Driver registered (201) or email already exists (200)': (r) => r.status === 201 || r.status === 200,
    });

    if (!isSuccessful) {
        console.log(`Registration failed! Status: ${res.status}, Body: ${res.body}`);
    }
}