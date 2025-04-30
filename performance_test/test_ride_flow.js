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

export function main() {
    sleep(Math.random() * 60 * 5); // Each VU starts at a random point in the x-minute window

    const baseUrl = 'http://localhost:8080/riderapp';

    const email = `rider${__VU}_${__ITER}@test.com`;
    const phone = `98765${Math.floor(Math.random() * 10000).toString().padStart(4, '0')}`;
    let x = Math.floor(Math.random() * 101);
    let y = Math.floor(Math.random() * 101);

    let res = http.post(`${baseUrl}/ride/rider/add?email=${email}&phoneNumber=${phone}&x=${x}&y=${y}`);
    check(res, { 'Registered successfully': (r) => r.status === 201 });
    const riderID = parseInt(res.body);
    if (!riderID) {
        console.error('Rider registration failed:', res.body);
        return;
    }

    sleep(1);

    res = http.get(`${baseUrl}/ride/rider/match?riderID=${riderID}`);
    check(res, { 'Matched rider': (r) => r.status === 200 });

    sleep(1);

    x = Math.floor(Math.random() * 101);
    y = Math.floor(Math.random() * 101);

    res = http.post(`${baseUrl}/ride/start?N=1&riderID=${riderID}&destination=Mall&x=${x}&y=${y}`);
    check(res, { 'Started ride': (r) => r.status === 200 });
    const rideID = res.json('rideID');
    if (!rideID) {
        console.log('Ride start failed:', res.json('error'));
        return;
    }

    sleep(1);

    res = http.post(`${baseUrl}/ride/stop?rideID=${rideID}&timeInMins=32`);
    check(res, { 'Stopped ride': (r) => r.status === 200 });

    sleep(1);

    res = http.get(`${baseUrl}/ride/bill?rideID=${rideID}`);
    check(res, { 'Generated bill': (r) => r.status === 200 });

    sleep(1);

    res = http.post(`${baseUrl}/payment/pay?rideID=${rideID}&type=CASH`);
    check(res, { 'Payment successful': (r) => r.status === 200 });

    sleep(1);
}
