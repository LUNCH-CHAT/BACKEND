import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
  stages: [
    { duration: '1m', target: 20 },    // 워밍업
    { duration: '3m', target: 100 },   // 정상 부하
    { duration: '1m', target: 200 },   // 피크 부하
    { duration: '1m', target: 0 },     // 쿨다운
  ],
  thresholds: {
    'http_req_duration{group:::Member API}': ['p(95)<1000'],
    'http_req_duration{group:::Recommendations}': ['p(95)<2000'], // 추천은 더 느릴 수 있음
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.9']
  }
};

const BASE_URL = __ENV.BASE_URL || 'http://nginx';

export default function () {
  const headers = {
    'Content-Type': 'application/json',
  };

  group('Member API', function () {
    // 1. 관심사 태그 조회
    let tagsResponse = http.get(`${BASE_URL}/api/tags`, { headers });
    check(tagsResponse, {
      'tags status is 200': (r) => r.status === 200,
      'tags response time < 500ms': (r) => r.timings.duration < 500,
      'tags has data': (r) => {
        try {
          const json = r.json();
          return json && json.data && Array.isArray(json.data);
        } catch (e) {
          return false;
        }
      }
    });

    // 2. 대학 정보 조회
    let collegeResponse = http.get(`${BASE_URL}/api/colleges`, { headers });
    check(collegeResponse, {
      'college status is 200 or 404': (r) => r.status === 200 || r.status === 404,
      'college response time < 300ms': (r) => r.timings.duration < 300,
    });

    // 3. 학과 정보 조회  
    let deptResponse = http.get(`${BASE_URL}/api/departments`, { headers });
    check(deptResponse, {
      'department status is 200 or 404': (r) => r.status === 200 || r.status === 404,
      'department response time < 300ms': (r) => r.timings.duration < 300,
    });
  });

  group('Recommendations', function () {
    // 4. 회원 추천 조회 (핵심 기능)
    let recommendationsResponse = http.get(
      `${BASE_URL}/api/members/recommendations?page=0&size=20`, 
      { headers }
    );
    check(recommendationsResponse, {
      'recommendations accessible': (r) => r.status === 200 || r.status === 401,
      'recommendations response < 2s': (r) => r.timings.duration < 2000,
    });

    // 5. 인기 회원 조회
    let popularResponse = http.get(`${BASE_URL}/api/members/popular`, { headers });
    check(popularResponse, {
      'popular members accessible': (r) => r.status === 200 || r.status === 401,
      'popular response < 1s': (r) => r.timings.duration < 1000,
    });
  });

  group('Notifications', function () {
    // 6. 알림 조회
    let notificationsResponse = http.get(`${BASE_URL}/api/notifications`, { headers });
    check(notificationsResponse, {
      'notifications accessible': (r) => r.status === 200 || r.status === 401,
      'notifications response time < 500ms': (r) => r.timings.duration < 500,
    });
  });

  sleep(Math.random() * 2 + 1); // 1-3초 랜덤 대기
}

export function handleSummary(data) {
  return {
    'k6/results/member-api-test-summary.json': JSON.stringify(data),
    'k6/results/member-api-test-summary.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}

function htmlReport(data) {
  return `
    <!DOCTYPE html>
    <html>
    <head><title>Member API Performance Test Results</title></head>
    <body>
      <h1>LunchChat Member API Performance Test</h1>
      <h2>Summary</h2>
      <p>Total Requests: ${data.metrics.http_reqs.values.count}</p>
      <p>Failed Requests: ${data.metrics.http_req_failed.values.rate * 100}%</p>
      <p>Average Response Time: ${data.metrics.http_req_duration.values.avg}ms</p>
      <p>95th Percentile: ${data.metrics.http_req_duration.values['p(95)']}ms</p>
      <p>Test Duration: ${data.state.testRunDurationMs}ms</p>
    </body>
    </html>
  `;
}