import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// 추천 시스템 성능 테스트 (가장 중요한 기능)
export const options = {
  stages: [
    { duration: '1m', target: 5 },    // 워밍업
    { duration: '3m', target: 20 },   // 일반 부하  
    { duration: '2m', target: 50 },   // 피크 부하
    { duration: '1m', target: 0 },    // 쿨다운
  ],
  thresholds: {
    'http_req_duration{endpoint:recommendations}': ['p(95)<3000'], // AI 추천은 3초까지 허용
    'http_req_duration{endpoint:popular}': ['p(95)<1000'],
    'http_req_duration{endpoint:filters}': ['p(95)<2000'],
    http_req_failed: ['rate<0.05'], // 에러율 5% 미만
    checks: ['rate>0.9']
  }
};

const BASE_URL = 'http://localhost:80'; // nginx 로드밸런서 테스트

export default function () {
  // 1. 멤버 추천 조회 (AI 기반 - 가장 무거운 쿼리)
  let recommendationsResponse = http.get(
    `${BASE_URL}/api/members/recommendations?page=0&size=20`,
    { tags: { endpoint: 'recommendations' } }
  );
  check(recommendationsResponse, {
    '추천 시스템 응답': (r) => r.status === 200 || r.status === 401,
    '추천 응답시간 < 3초': (r) => r.timings.duration < 3000,
  });

  sleep(1);

  // 2. 인기 회원 조회 (캐시된 데이터)
  let popularResponse = http.get(
    `${BASE_URL}/api/members/popular?page=0&size=10`,
    { tags: { endpoint: 'popular' } }
  );
  check(popularResponse, {
    '인기 회원 응답': (r) => r.status === 200 || r.status === 401,
    '인기 회원 < 1초': (r) => r.timings.duration < 1000,
  });

  sleep(1);

  // 3. 필터링된 회원 검색
  let filtersResponse = http.get(
    `${BASE_URL}/api/members/filters?universityId=1&interests=개발&page=0&size=20`,
    { tags: { endpoint: 'filters' } }
  );
  check(filtersResponse, {
    '필터링 검색 응답': (r) => r.status === 200 || r.status === 401,
    '필터링 < 2초': (r) => r.timings.duration < 2000,
  });

  sleep(2);
}

export function handleSummary(data) {
  return {
    'k6/results/recommendation-test.json': JSON.stringify(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}