import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  scenarios: {
          random_load: {
              executor: 'per-vu-iterations',
              vus: 1000,
              iterations: 1,
              maxDuration: '1m',
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

export function main() {
  const x = randomInt(0, 100);
  const y = randomInt(0, 100);
  const phoneNumber = randomPhone();
  const email = randomEmail(randomInt(1, 10000));

  sleep(Math.random() * 60 * 1); // Each VU starts at a random point in the x-minute window

  http.post(`http://localhost:8080/riderapp/driver/add?email=${email}&phoneNumber=${phoneNumber}&x=${x}&y=${y}`);
}
