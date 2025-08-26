-- =======================================
-- LunchChat K6 Performance Test Mock Data (Clean Version)
-- =======================================
-- 이 스크립트는 빈 데이터베이스를 기준으로 k6 성능 테스트용 목 데이터를 생성합니다.
-- 모든 엔티티 구조를 정확히 분석해서 오류 없이 실행되도록 작성되었습니다.

-- =======================================
-- 1. 대학교 데이터 (University)
-- =======================================
INSERT INTO university (name, domain) VALUES 
('UMC대', 'umc.edu'),
('이화여자대학교', 'ewha.ac.kr'),
('한국항공대학교', 'kau.kr'),
('가톨릭대학교', 'catholic.ac.kr'),
('테스트대학교', 'test.com'),
('성능테스트대학교', 'performance.test.com');

-- =======================================
-- 2. 단과대학 데이터 (College)  
-- =======================================
INSERT INTO college (name, university_id) VALUES
-- 이화여자대학교 단과대학들 (고유명 사용)
('이화공과대학', 2),
('이화경영대학', 2),
('이화문과대학', 2),
('이화사회과학대학', 2),
('이화자연과학대학', 2),

-- 한국항공대학교 단과대학들  
('항공우주및기계공학부', 3),
('전자정보공학부', 3),
('항공교통물류학부', 3),

-- 가톨릭대학교 단과대학들 (고유명 사용)
('가톨릭공과대학', 4),
('가톨릭경영경제학부', 4),

-- 테스트 대학들
('테스트공대', 5),
('성능테스트학부', 6);

-- =======================================
-- 3. 학과 데이터 (Department)  
-- =======================================
INSERT INTO department (name, college_id) VALUES
-- 이화여자대학교 학과들
('컴퓨터공학과', 1),
('건축학과', 1),
('경영학과', 2),
('국어국문학과', 3),
('영어영문학과', 3),
('사회학과', 4),
('수학과', 5),
('화학과', 5),

-- 한국항공대학교 학과들
('항공우주공학과', 6),
('기계공학과', 6),
('전자공학과', 7),
('컴퓨터공학과', 7),

-- 가톨릭대학교 학과들  
('컴퓨터정보공학부', 9),
('경영학과', 10),

-- 테스트 학과들
('테스트학과', 11),
('성능테스트학과', 12);

-- =======================================
-- 4. 관심사 데이터 (Interest) 
-- =======================================
INSERT INTO interest (type) VALUES
('EXCHANGE_STUDENT'),
('EMPLOYMENT_CAREER'), 
('EXAM_PREPARATION'),
('STARTUP'),
('GPA_MANAGEMENT'),
('FOREIGN_LANGUAGE_STUDY'),
('HOBBY_LEISURE'),
('SCHOOL_LIFE');

-- =======================================
-- 5. 회원 데이터 (Member) - 성능 테스트용
-- =======================================
-- 실제 Member 엔티티 필드에 맞춘 정확한 컬럼명들 사용
INSERT INTO member (membername, email, password, role, student_no, profile_image_url, login_type, status, university_id, college_id, department_id, created_at, updated_at) 
VALUES 
-- k6 테스트용 기본 사용자들 (실제로는 JS에서 동적 생성)
-- 여기서는 추천 알고리즘 테스트를 위한 기본 더미 데이터만 생성
('김민수', 'dummy001@ewha.ac.kr', '$2a$10$dummyHashForRecommendationTestOnly', 'ROLE_USER', '2021001001', 'https://test-profile1.jpg', 'Direct', 'ACTIVE', 2, 1, 1, NOW(), NOW()),
('이영희', 'k6test002@ewha.ac.kr', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021001002', 'https://test-profile2.jpg', 'Direct', 'ACTIVE', 2, 2, 3, NOW(), NOW()),
('박철수', 'k6test003@ewha.ac.kr', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021001003', 'https://test-profile3.jpg', 'Direct', 'ACTIVE', 2, 1, 1, NOW(), NOW()),
('최지은', 'k6test004@kau.kr', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021002001', 'https://test-profile4.jpg', 'Direct', 'ACTIVE', 3, 6, 9, NOW(), NOW()),
('정민호', 'k6test005@kau.kr', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021002002', 'https://test-profile5.jpg', 'Direct', 'ACTIVE', 3, 7, 11, NOW(), NOW()),
('한소영', 'k6test006@catholic.ac.kr', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021003001', 'https://test-profile6.jpg', 'Direct', 'ACTIVE', 4, 9, 13, NOW(), NOW()),
('윤태현', 'k6test007@catholic.ac.kr', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021003002', 'https://test-profile7.jpg', 'Direct', 'ACTIVE', 4, 10, 14, NOW(), NOW()),

-- 추가 더미 사용자들 (추천 알고리즘 테스트용)
('서준호', 'k6test008@test.com', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021004001', 'https://test-profile8.jpg', 'Direct', 'ACTIVE', 5, 11, 15, NOW(), NOW()),
('김하나', 'k6test009@test.com', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021004002', 'https://test-profile9.jpg', 'Direct', 'ACTIVE', 5, 11, 15, NOW(), NOW()),
('이성민', 'k6test010@performance.test.com', '$2a$10$K.z2YnmFwS2N7JjjzLPwRuHx5P0HjBfOz5.JjjzLPwRuHx5P0HjBfO', 'ROLE_USER', '2021005001', 'https://test-profile10.jpg', 'Direct', 'ACTIVE', 6, 12, 16, NOW(), NOW());

-- =======================================
-- 6. 회원-관심사 연결 테이블 (member_interest) - ManyToMany 관계
-- =======================================
INSERT INTO member_interest (member_id, interest_id) VALUES
-- 김민수의 관심사
(1, 2), -- EMPLOYMENT_CAREER
(1, 4), -- STARTUP
(1, 8), -- SCHOOL_LIFE

-- 이영희의 관심사
(2, 2), -- EMPLOYMENT_CAREER
(2, 5), -- GPA_MANAGEMENT
(2, 7), -- HOBBY_LEISURE

-- 박철수의 관심사  
(3, 3), -- EXAM_PREPARATION
(3, 4), -- STARTUP
(3, 2), -- EMPLOYMENT_CAREER

-- 최지은의 관심사
(4, 1), -- EXCHANGE_STUDENT
(4, 6), -- FOREIGN_LANGUAGE_STUDY
(4, 8), -- SCHOOL_LIFE

-- 정민호의 관심사
(5, 2), -- EMPLOYMENT_CAREER
(5, 4), -- STARTUP
(5, 5), -- GPA_MANAGEMENT

-- 한소영의 관심사
(6, 5), -- GPA_MANAGEMENT
(6, 7), -- HOBBY_LEISURE
(6, 8), -- SCHOOL_LIFE

-- 윤태현의 관심사
(7, 2), -- EMPLOYMENT_CAREER
(7, 3), -- EXAM_PREPARATION
(7, 6); -- FOREIGN_LANGUAGE_STUDY

-- =======================================
-- 7. 시간표 데이터 (TimeTable) - 추천 알고리즘 성능 테스트
-- =======================================
-- 실제 TimeTable 엔티티의 필드명에 맞춤 (day_of_week, start_time, end_time, subject_name)
INSERT INTO time_table (member_id, subject_name, day_of_week, start_time, end_time, created_at, updated_at) VALUES
-- 김민수의 시간표 (member_id=1)
(1, '데이터베이스시스템', 'MON', '09:00:00', '10:30:00', NOW(), NOW()),
(1, '소프트웨어공학', 'WED', '10:30:00', '12:00:00', NOW(), NOW()),
(1, '컴퓨터네트워크', 'FRI', '14:00:00', '15:30:00', NOW(), NOW()),

-- 이영희의 시간표 (member_id=2)
(2, '경영전략론', 'TUE', '09:00:00', '10:30:00', NOW(), NOW()),
(2, '마케팅원론', 'THU', '13:00:00', '14:30:00', NOW(), NOW()),
(2, '회계학원리', 'FRI', '10:30:00', '12:00:00', NOW(), NOW()),

-- 박철수의 시간표 (member_id=3) - 김민수와 일부 겹침
(3, '알고리즘', 'MON', '10:30:00', '12:00:00', NOW(), NOW()),
(3, '데이터베이스시스템', 'MON', '09:00:00', '10:30:00', NOW(), NOW()), -- 김민수와 동일 시간
(3, '기계학습', 'WED', '14:00:00', '15:30:00', NOW(), NOW()),

-- 최지은의 시간표 (member_id=4)
(4, '항공역학', 'MON', '13:00:00', '14:30:00', NOW(), NOW()),
(4, '유체역학', 'TUE', '10:30:00', '12:00:00', NOW(), NOW()),
(4, '구조역학', 'THU', '09:00:00', '10:30:00', NOW(), NOW());

-- =======================================
-- 8. 사용자 키워드 데이터 (UserKeyword) - 추천 알고리즘 테스트
-- =======================================
-- 실제 UserKeyword 엔티티 필드명에 맞춤 (type은 KeywordType enum)
INSERT INTO user_keyword (member_id, type, title, description, created_at, updated_at) VALUES
-- 김민수의 키워드 (member_id=1)
(1, 'EXPRESS', '활발한 성격', '에너지가 넘치고 적극적인 성격입니다', NOW(), NOW()),
(1, 'GOAL', '취업 준비', '대기업 개발자 취업이 목표입니다', NOW(), NOW()),
(1, 'INTEREST', '프로그래밍', '새로운 기술 학습을 좋아합니다', NOW(), NOW()),

-- 이영희의 키워드 (member_id=2)
(2, 'EXPRESS', '계획적', '체계적이고 꼼꼼한 성격입니다', NOW(), NOW()),
(2, 'GOAL', '경영 전문가', '마케팅 분야 전문가가 되고 싶습니다', NOW(), NOW()),
(2, 'INTEREST', '독서', '경영 서적과 자기계발서를 즐겨 읽습니다', NOW(), NOW()),

-- 박철수의 키워드 (member_id=3)  
(3, 'EXPRESS', '분석적', '논리적 사고를 중시합니다', NOW(), NOW()),
(3, 'GOAL', '대학원 진학', '컴퓨터과학 석사 과정 진학 예정입니다', NOW(), NOW()),
(3, 'INTEREST', '연구', '알고리즘과 AI 연구에 관심이 많습니다', NOW(), NOW()),

-- 최지은의 키워드 (member_id=4)
(4, 'EXPRESS', '도전적', '새로운 도전을 두려워하지 않습니다', NOW(), NOW()),
(4, 'GOAL', '항공우주 엔지니어', '우주항공 분야에서 일하고 싶습니다', NOW(), NOW()),
(4, 'INTEREST', '우주과학', '우주 탐사와 항공기술에 관심이 많습니다', NOW(), NOW());

-- =======================================
-- 9. 매칭 데이터 (Matches) - 동시성 테스트용
-- =======================================
INSERT INTO matches (from_member_id, to_member_id, status, created_at) VALUES
(1, 2, 'REQUESTED', NOW()),
(1, 3, 'ACCEPTED', NOW()),
(2, 4, 'REQUESTED', NOW()),
(3, 5, 'REQUESTED', NOW()),
(4, 6, 'ACCEPTED', NOW()),
(5, 7, 'REQUESTED', NOW()),
(6, 1, 'REQUESTED', NOW()),
(7, 2, 'ACCEPTED', NOW()),
(8, 9, 'REQUESTED', NOW()),
(9, 10, 'ACCEPTED', NOW());

-- =======================================
-- 10. 알림 데이터 (Notification) - MongoDB + MySQL 조합 테스트
-- =======================================
INSERT INTO notification (user_id, sender_id, type, content, is_read, created_at, updated_at) VALUES
(2, 1, 'MATCH_REQUEST', '김민수님이 매칭을 요청했습니다', false, NOW(), NOW()),
(3, 1, 'MATCH_REQUEST', '김민수님이 매칭을 요청했습니다', true, NOW(), NOW()),
(4, 2, 'MATCH_REQUEST', '이영희님이 매칭을 요청했습니다', false, NOW(), NOW()),
(1, 3, 'MATCH_ACCEPTED', '박철수님이 매칭을 수락했습니다', false, NOW(), NOW()),
(6, 4, 'MATCH_ACCEPTED', '최지은님이 매칭을 수락했습니다', true, NOW(), NOW()),
(2, 7, 'MATCH_ACCEPTED', '윤태현님이 매칭을 수락했습니다', false, NOW(), NOW()),
(9, 8, 'MATCH_REQUEST', '서준호님이 매칭을 요청했습니다', false, NOW(), NOW()),
(10, 9, 'MATCH_ACCEPTED', '김하나님이 매칭을 수락했습니다', true, NOW(), NOW());

-- =======================================
-- 11. 사용자 통계 데이터 (UserStatistics) - 인기 회원 조회 테스트
-- =======================================
-- 실제 UserStatistics 엔티티 구조에 맞춤 (member_id, match_requested_count, match_received_count, match_completed_count)
INSERT INTO user_statistics (member_id, match_requested_count, match_received_count, match_completed_count, created_at, updated_at) VALUES
(1, 5, 8, 3, NOW(), NOW()),    -- 김민수
(2, 8, 12, 5, NOW(), NOW()),   -- 이영희  
(3, 3, 6, 2, NOW(), NOW()),    -- 박철수
(4, 12, 20, 8, NOW(), NOW()),  -- 최지은 (인기 사용자)
(5, 7, 15, 4, NOW(), NOW()),   -- 정민호
(6, 15, 25, 10, NOW(), NOW()), -- 한소영 (최고 인기 사용자)
(7, 6, 10, 3, NOW(), NOW()),   -- 윤태현
(8, 4, 7, 2, NOW(), NOW()),    -- 서준호
(9, 9, 18, 6, NOW(), NOW()),   -- 김하나
(10, 2, 5, 1, NOW(), NOW());   -- 이성민