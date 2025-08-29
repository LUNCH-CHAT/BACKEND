-- 1단계: 기본 인덱스 최적화 (즉시 적용 가능)

-- 회원 추천 API 최적화를 위한 복합 인덱스
-- 관심사 + 상태 + 생성일 기준으로 추천 쿼리 최적화
DROP INDEX IF EXISTS idx_member_recommendation_optimization ON member;
CREATE INDEX idx_member_recommendation_optimization ON member(status, university_id, college_id, created_at DESC);

-- 인기 회원 API 최적화를 위한 인덱스
-- 매칭 수 집계 쿼리 최적화
DROP INDEX IF EXISTS idx_matches_aggregation ON matches;
CREATE INDEX idx_matches_aggregation ON matches(to_member_id, status, created_at DESC);

-- 필터링 API 최적화를 위한 복합 인덱스
-- 필터 조건들을 효율적으로 처리하기 위한 인덱스
DROP INDEX IF EXISTS idx_member_filter_optimization ON member;
CREATE INDEX idx_member_filter_optimization ON member(status, college_id, department_id, updated_at DESC);

-- 관심사 기반 필터링을 위한 인덱스
DROP INDEX IF EXISTS idx_member_interest_filter ON member_interest;
CREATE INDEX idx_member_interest_filter ON member_interest(interest_id, member_id);

-- 시간표 기반 매칭을 위한 인덱스
DROP INDEX IF EXISTS idx_timetable_matching ON time_table;
CREATE INDEX idx_timetable_matching ON time_table(member_id, day_of_week, start_time, end_time);

-- 2단계: 쿼리 성능 최적화 인덱스

-- N+1 문제 해결을 위한 인덱스
-- 회원 + 관심사 + 시간표 조인 최적화
DROP INDEX IF EXISTS idx_user_keyword_efficient ON user_keyword;
CREATE INDEX idx_user_keyword_efficient ON user_keyword(member_id, type, title);

-- 채팅방 성능 최적화
-- 채팅방 목록 조회 최적화
DROP INDEX IF EXISTS idx_chatroom_efficient ON chat_room;
CREATE INDEX idx_chatroom_efficient ON chat_room(starter_id, friend_id, last_message_send_at DESC);

-- 알림 시스템 최적화
-- 읽지 않은 알림 조회 최적화
DROP INDEX IF EXISTS idx_notification_efficient ON notification;
CREATE INDEX idx_notification_efficient ON notification(user_id, is_read, created_at DESC);