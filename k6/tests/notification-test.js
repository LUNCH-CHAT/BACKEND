import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// 알림 시스템 성능 테스트
export const options = {
  stages: [
    { duration: '1m', target: 15 },    // 워밍업
    { duration: '3m', target: 40 },    // 일반 부하
    { duration: '1m', target: 80 },    // 피크 부하 (알림 폭증)
    { duration: '1m', target: 0 },     // 쿨다운
  ],
  thresholds: {
    'http_req_duration{endpoint:notification_list}': ['p(95)<1000'],
    'http_req_duration{endpoint:fcm_token}': ['p(95)<500'],
    http_req_failed: ['rate<0.02'], // 알림은 2% 에러율까지 허용
    checks: ['rate>0.95']
  }
};

const BASE_URL = 'http://localhost:80';

export default function () {
  const headers = {
    'Content-Type': 'application/json',
  };

  // 1. 알림 목록 조회 (커서 페이지네이션)
  let notificationResponse = http.get(
    `${BASE_URL}/api/notifications?size=20&cursor=${Date.now()}`,
    { tags: { endpoint: 'notification_list' } }
  );
  check(notificationResponse, {
    '알림 목록 응답': (r) => r.status === 200 || r.status === 401,
    '알림 목록 < 1초': (r) => r.timings.duration < 1000,
    '알림 데이터 구조 확인': (r) => {
      try {
        const json = r.json();
        return json && (json.data !== undefined || r.status === 401);
      } catch (e) {
        return false;
      }
    }
  });

  sleep(1);

  // 2. FCM 토큰 업데이트 (모바일 푸시 알림용)
  const fcmPayload = JSON.stringify({
    fcmToken: `test_token_${__VU}_${Date.now()}`,
    deviceType: 'ANDROID'
  });

  let fcmResponse = http.post(
    `${BASE_URL}/api/notifications/fcm/token`,
    fcmPayload,
    { 
      headers,
      tags: { endpoint: 'fcm_token' }
    }
  );
  check(fcmResponse, {
    'FCM 토큰 업데이트': (r) => r.status === 200 || r.status === 401,
    'FCM 토큰 < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(1);

  // 3. 읽지 않은 알림 개수 조회
  let unreadResponse = http.get(
    `${BASE_URL}/api/notifications/unread/count`
  );
  check(unreadResponse, {
    '읽지않은 알림 개수': (r) => r.status === 200 || r.status === 401,
    '개수 조회 < 300ms': (r) => r.timings.duration < 300,
  });

  // 4. 특정 알림 읽음 처리 (PATCH)
  const notificationId = Math.floor(Math.random() * 100) + 1;
  let markReadResponse = http.patch(
    `${BASE_URL}/api/notifications/${notificationId}/read`,
    null,
    { headers }
  );
  check(markReadResponse, {
    '알림 읽음 처리': (r) => r.status === 200 || r.status === 404 || r.status === 401,
    '읽음 처리 < 400ms': (r) => r.timings.duration < 400,
  });

  sleep(2);
}

export function handleSummary(data) {
  const notificationMetrics = {
    '총 알림 API 호출': data.metrics.http_reqs ? data.metrics.http_reqs.values.count : 0,
    '평균 응답시간': data.metrics.http_req_duration ? `${data.metrics.http_req_duration.values.avg}ms` : '0ms',
    '95% 응답시간': data.metrics.http_req_duration ? `${data.metrics.http_req_duration.values['p(95)']}ms` : '0ms',
    '에러율': data.metrics.http_req_failed ? `${(data.metrics.http_req_failed.values.rate * 100).toFixed(2)}%` : '0%',
  };

  return {
    'k6/results/notification-test.json': JSON.stringify(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }) + '\n\n🔔 알림 시스템 요약:\n' + Object.entries(notificationMetrics).map(([k, v]) => `  ${k}: ${v}`).join('\n'),
  };
}