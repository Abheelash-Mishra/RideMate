import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 10000,
  iterations: 10000,
};

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomPhone() {
  return '9' + Math.floor(100000000 + Math.random() * 900000000); // 10-digit Indian style
}

function randomEmail(index) {
  return `driver${index}@email.com`;
}

export default function () {
  const x = randomInt(0, 100);
  const y = randomInt(0, 100);
  const phoneNumber = randomPhone();
  const email = randomEmail(randomInt(1, 10000));

  http.post(`http://localhost:8080/riderapp/driver/add?email=${email}&phoneNumber=${phoneNumber}&x=${x}&y=${y}`);
}
