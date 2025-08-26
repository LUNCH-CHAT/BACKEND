import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
  stages: [
    { duration: '30s', target: 10 },    // 워밍업 
    { duration: '2m', target: 50 },     // 중간 부하
    { duration: '3m', target: 100 },    // 높은 부하 (MySQL 커넥션 풀 테스트)
    { duration: '2m', target: 200 },    // 피크 부하 (동시성 테스트)
    { duration: '1m', target: 0 },      // 쿨다운
  ],
  thresholds: {
    // MySQL 성능 최적화 목적의 임계값
    'http_req_duration{group:::Database Heavy Operations}': ['p(95)<2000'],
    'http_req_duration{group:::Member Recommendations}': ['p(95)<3000'],  // 복잡한 추천 알고리즘
    'http_req_duration{group:::Concurrent Operations}': ['p(95)<1500'],   // 동시성 테스트
    http_req_failed: ['rate<0.05'],  // 5% 까지 에러 허용 (높은 부하에서)
    checks: ['rate>0.85']            // 85% 이상 성공
  }
};

const BASE_URL = __ENV.BASE_URL || 'http://nginx';

// 테스트 사용자 인증 - 새로운 사용자를 생성해서 테스트
export function setup() {
  console.log('🔐 Setting up authenticated users for database stress test...');
  
  const tokens = [];
  const maxUsers = 100; // 과부하 테스트를 위한 충분한 사용자 수

  for (let i = 0; i < maxUsers; i++) {
    const email = `perftest${i}@k6test.com`;
    const password = 'test123'; // 간단한 비밀번호
    
    // 회원가입 시도
    const registerResponse = http.post(`${BASE_URL}/auth/register/direct`, 
      JSON.stringify({
        email: email,
        password: password
      }), 
      {
        headers: { 'Content-Type': 'application/json' }
      }
    );

    if (registerResponse.status === 200) {
      const responseData = registerResponse.json();
      if (responseData.data && responseData.data.accessToken) {
        tokens.push({
          accessToken: responseData.data.accessToken,
          email: email
        });
        console.log(`✅ DB Test User ${i} registered: ${email}`);
      }
    } else {
      // 이미 존재하는 사용자일 경우 로그인 시도
      const loginResponse = http.post(`${BASE_URL}/auth/login/direct`, 
        JSON.stringify({
          email: email,
          password: password
        }), 
        {
          headers: { 'Content-Type': 'application/json' }
        }
      );

      if (loginResponse.status === 200) {
        const responseData = loginResponse.json();
        if (responseData.data && responseData.data.accessToken) {
          tokens.push({
            accessToken: responseData.data.accessToken,
            email: email
          });
          console.log(`✅ DB Test User ${i} logged in: ${email}`);
        }
      }
    }
    
    sleep(0.05); // 빠른 사용자 생성
  }

  console.log(`🎯 Successfully authenticated ${tokens.length} users for database testing`);
  return { tokens };
}

export default function (data) {
  if (!data.tokens || data.tokens.length === 0) {
    console.error('❌ No authenticated tokens available for database testing');
    return;
  }

  // VU별로 다른 토큰 사용
  const tokenIndex = (__VU - 1) % data.tokens.length;
  const userToken = data.tokens[tokenIndex];
  
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${userToken.accessToken}`
  };

  // 1. 데이터베이스 집약적 작업들
  group('Database Heavy Operations', function () {
    // 회원 추천 알고리즘 (복잡한 JOIN과 계산) - 페이징 파라미터 없음
    let recommendationsResponse = http.get(
      `${BASE_URL}/api/members/recommendations`, 
      { headers }
    );
    check(recommendationsResponse, {
      'recommendations query successful': (r) => r.status === 200,
      'recommendations response < 3s': (r) => r.timings.duration < 3000,
    });

    // 인기 회원 조회 (통계 계산)
    let popularResponse = http.get(`${BASE_URL}/api/members/popular`, { headers });
    check(popularResponse, {
      'popular members query successful': (r) => r.status === 200,
      'popular response < 1.5s': (r) => r.timings.duration < 1500,
    });

    // 필터 기반 추천 조회 (복잡한 쿼리 테스트)
    let filterResponse = http.get(`${BASE_URL}/api/members/filters?page=0&size=10&gender=MALE&grade=3`, { headers });
    check(filterResponse, {
      'filter query successful': (r) => r.status === 200,
      'filter response < 2s': (r) => r.timings.duration < 2000,
    });

    // 마이페이지 조회 (사용자별 복합 데이터)
    let mypageResponse = http.get(`${BASE_URL}/api/members/mypage`, { headers });
    check(mypageResponse, {
      'mypage query successful': (r) => r.status === 200,
      'mypage response < 1s': (r) => r.timings.duration < 1000,
    });
  });

  // 2. 동시성 테스트 (매칭 관련)
  group('Concurrent Operations', function () {
    // 받은 매칭 요청 조회 (실제 API 구조에 맞춤)
    let receivedMatchesResponse = http.get(`${BASE_URL}/api/matches?status=RECEIVED&page=0&size=10`, { headers });
    check(receivedMatchesResponse, {
      'received matches query successful': (r) => r.status === 200,
      'received matches response < 1s': (r) => r.timings.duration < 1000,
    });

    // 보낸 매칭 요청 조회  
    let requestedMatchesResponse = http.get(`${BASE_URL}/api/matches?status=REQUESTED&page=0&size=10`, { headers });
    check(requestedMatchesResponse, {
      'requested matches query successful': (r) => r.status === 200,
      'requested matches response < 1s': (r) => r.timings.duration < 1000,
    });

    // 수락된 매칭 조회
    let acceptedMatchesResponse = http.get(`${BASE_URL}/api/matches?status=ACCEPTED&page=0&size=10`, { headers });
    check(acceptedMatchesResponse, {
      'accepted matches query successful': (r) => r.status === 200,
      'accepted matches response < 1s': (r) => r.timings.duration < 1000,
    });

    // 알림 조회 (MongoDB + MySQL 조합)
    let notificationsResponse = http.get(`${BASE_URL}/api/notifications`, { headers });
    check(notificationsResponse, {
      'notifications query successful': (r) => r.status === 200,
      'notifications response < 800ms': (r) => r.timings.duration < 800,
    });
  });

  // 3. 빠른 조회 테스트 (캐시 효과)
  group('Quick Lookups', function () {
    // 내 키워드 조회 (빠른 응답 테스트)
    let keywordsResponse = http.get(`${BASE_URL}/api/members/keywords`, { headers });
    check(keywordsResponse, {
      'keywords query successful': (r) => r.status === 200,
      'keywords fast response < 300ms': (r) => r.timings.duration < 300,
    });

    // 내 프로필 상세 조회
    let myProfileResponse = http.get(`${BASE_URL}/api/members/me`, { headers });
    check(myProfileResponse, {
      'my profile query successful': (r) => r.status === 200,
      'my profile response < 500ms': (r) => r.timings.duration < 500,
    });

    // 특정 멤버 상세 조회 (랜덤 ID로 테스트)
    const randomMemberId = Math.floor(Math.random() * 10) + 1; // 1~10 범위
    let memberDetailResponse = http.get(`${BASE_URL}/api/members/${randomMemberId}`, { headers });
    check(memberDetailResponse, {
      'member detail query processed': (r) => r.status === 200 || r.status === 404, // 존재하지 않을 수도 있음
      'member detail response < 800ms': (r) => r.timings.duration < 800,
    });
  });

  // 다양한 대기 패턴으로 실제 사용자 시뮬레이션
  const waitTime = Math.random() * 3 + 1; // 1-4초 랜덤 대기
  sleep(waitTime);
}

export function handleSummary(data) {
  const avgResponseTime = data.metrics.http_req_duration.values.avg;
  const p95ResponseTime = data.metrics.http_req_duration.values['p(95)'];
  const errorRate = data.metrics.http_req_failed.values.rate * 100;
  
  return {
    'k6/results/database-intensive-summary.json': JSON.stringify(data),
    'k6/results/database-intensive-summary.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}

function htmlReport(data) {
  const passRate = data.metrics.checks.values.rate * 100;
  const avgResponseTime = data.metrics.http_req_duration.values.avg;
  const p95ResponseTime = data.metrics.http_req_duration.values['p(95)'];
  const errorRate = data.metrics.http_req_failed.values.rate * 100;
  
  return `
    <!DOCTYPE html>
    <html>
    <head>
      <title>🗄️ LunchChat Database Intensive Performance Test</title>
      <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; }
        .metric { margin: 15px 0; padding: 10px; background: #f8f9fa; border-radius: 4px; }
        .success { color: #28a745; font-weight: bold; }
        .warning { color: #ffc107; font-weight: bold; }
        .danger { color: #dc3545; font-weight: bold; }
        .header { color: #007bff; border-bottom: 2px solid #007bff; padding-bottom: 10px; }
      </style>
    </head>
    <body>
      <div class="container">
        <h1 class="header">🗄️ LunchChat Database Intensive Performance Test</h1>
        <h2>📊 Performance Metrics</h2>
        
        <div class="metric">
          <strong>🚀 Total Requests:</strong> ${data.metrics.http_reqs.values.count}
        </div>
        
        <div class="metric">
          <strong>❌ Error Rate:</strong> 
          <span class="${errorRate < 5 ? 'success' : 'danger'}">
            ${errorRate.toFixed(2)}%
          </span>
          ${errorRate < 5 ? '✅ Excellent' : errorRate < 10 ? '⚠️ Needs attention' : '🚨 Critical'}
        </div>
        
        <div class="metric">
          <strong>✅ Check Pass Rate:</strong> 
          <span class="${passRate > 85 ? 'success' : 'warning'}">
            ${passRate.toFixed(2)}%
          </span>
        </div>
        
        <div class="metric">
          <strong>⚡ Average Response Time:</strong> 
          <span class="${avgResponseTime < 1000 ? 'success' : avgResponseTime < 2000 ? 'warning' : 'danger'}">
            ${avgResponseTime.toFixed(2)}ms
          </span>
        </div>
        
        <div class="metric">
          <strong>🎯 95th Percentile Response:</strong> 
          <span class="${p95ResponseTime < 2000 ? 'success' : p95ResponseTime < 3000 ? 'warning' : 'danger'}">
            ${p95ResponseTime.toFixed(2)}ms
          </span>
        </div>
        
        <div class="metric">
          <strong>⏱️ Test Duration:</strong> ${(data.state.testRunDurationMs / 1000).toFixed(2)}s
        </div>

        <h2>🎯 Database Performance Thresholds</h2>
        <ul>
          <li><strong>Database Heavy Operations (95th percentile):</strong> < 2000ms</li>
          <li><strong>Member Recommendations (95th percentile):</strong> < 3000ms</li>
          <li><strong>Concurrent Operations (95th percentile):</strong> < 1500ms</li>
          <li><strong>Error Rate:</strong> < 5%</li>
          <li><strong>Check Success Rate:</strong> > 85%</li>
        </ul>

        <h2>🔍 Test Focus Areas</h2>
        <ul>
          <li>🗄️ MySQL Connection Pool Performance</li>
          <li>🔄 Concurrent Database Access</li>
          <li>📈 Complex Query Performance (Recommendations)</li>
          <li>⚡ Caching Effectiveness</li>
          <li>🔒 Transaction Integrity</li>
        </ul>
        
        <p><em>🕐 Generated at: ${new Date().toISOString()}</em></p>
      </div>
    </body>
    </html>
  `;
}