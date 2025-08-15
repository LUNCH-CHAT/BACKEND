-- ===========================================
-- LunchChat V6 Initial Data Insertion Script
-- ===========================================

-- 1. 관심사 (Interest) 테이블 데이터 삽입
-- created_at, updated_at 값을 현재 시간으로 설정
INSERT INTO interest (id, type, created_at, updated_at) VALUES
(1, 'EXCHANGE_STUDENT', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(2, 'EMPLOYMENT_CAREER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(3, 'EXAM_PREPARATION', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(4, 'STARTUP', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(5, 'GPA_MANAGEMENT', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(6, 'FOREIGN_LANGUAGE_STUDY', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(7, 'HOBBY_LEISURE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(8, 'SCHOOL_LIFE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- 2. 대학교 (University) 테이블 데이터 삽입
-- created_at, updated_at 값을 현재 시간으로 설정
INSERT INTO university (id, name, domain, created_at, updated_at) VALUES
(1, '가톨릭대', 'catholic.ac.kr', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(2, '한국항공대', 'kau.kr', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(3, '이화여대', 'ewha.ac.kr', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(4, '이화여대', 'ewhain.net', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(5, 'UMC대', '', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
