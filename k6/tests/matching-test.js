import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// 매칭 시스템 성능 테스트
export const options = {
  stages: [
    { duration: '30s', target: 10 },   // 워밍업
    { duration: '2m', target: 30 },    // 일반 부하
    { duration: '1m', target: 60 },    // 피크 부하
    { duration: '30s', target: 0 },    // 쿨다운
  ],
  thresholds: {
    'http_req_duration{endpoint:match_create}': ['p(95)<1000'],
    'http_req_duration{endpoint:match_list}': ['p(95)<800'],
    'http_req_duration{endpoint:match_status}': ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.95']
  }
};

const BASE_URL = 'http://localhost:80';

export default function () {
  const headers = {
    'Content-Type': 'application/json',
  };

  // 1. 매칭 요청 생성 (POST)
  const matchPayload = JSON.stringify({
    toMemberId: Math.floor(Math.random() * 100) + 1, // 랜덤 사용자
    message: `매칭 요청 테스트 - ${Date.now()}`
  });

  let createMatchResponse = http.post(
    `${BASE_URL}/api/matches`,
    matchPayload,
    { 
      headers,
      tags: { endpoint: 'match_create' }
    }
  );
  check(createMatchResponse, {
    '매칭 생성 응답': (r) => r.status === 200 || r.status === 201 || r.status === 401,
    '매칭 생성 < 1초': (r) => r.timings.duration < 1000,
  });

  sleep(1);

  // 2. 내 매칭 목록 조회
  let matchListResponse = http.get(
    `${BASE_URL}/api/matches?status=PENDING&page=0&size=20`,
    { tags: { endpoint: 'match_list' } }
  );
  check(matchListResponse, {
    '매칭 목록 응답': (r) => r.status === 200 || r.status === 401,
    '매칭 목록 < 800ms': (r) => r.timings.duration < 800,
  });

  sleep(1);

  // 3. 매칭 상태 확인
  let statusResponse = http.get(
    `${BASE_URL}/api/matches/status/1`, // 매칭 ID 1
    { tags: { endpoint: 'match_status' } }
  );
  check(statusResponse, {
    '매칭 상태 응답': (r) => r.status === 200 || r.status === 404 || r.status === 401,
    '매칭 상태 < 500ms': (r) => r.timings.duration < 500,
  });

  // 4. 멘토/멘티 매칭 조회
  let mentorResponse = http.get(
    `${BASE_URL}/api/mentors?page=0&size=10`
  );
  check(mentorResponse, {
    '멘토 목록 응답': (r) => r.status === 200 || r.status === 401,
    '멘토 목록 < 1초': (r) => r.timings.duration < 1000,
  });

  sleep(2);
}

export function handleSummary(data) {
  return {
    'k6/results/matching-test.json': JSON.stringify(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}