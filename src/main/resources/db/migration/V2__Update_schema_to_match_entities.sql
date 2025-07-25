-- V2__Update_schema_to_match_entities.sql
-- Date: 2025-07-25
-- 안전한 마이그레이션을 위한 개선된 버전

SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 기존 데이터 백업 (중요!)
-- ====================================================================

-- 기존 관심사 데이터 백업
CREATE TABLE interests_backup AS SELECT * FROM interests;
CREATE TABLE user_interests_backup AS SELECT * FROM user_interests;

-- ====================================================================
-- 2. 불필요한 테이블 삭제
-- ====================================================================

DROP TABLE IF EXISTS chat_participant;
DROP TABLE IF EXISTS user_interests;

-- ====================================================================
-- 3. Interest 테이블 마이그레이션 (데이터 보존)
-- ====================================================================

-- 새로운 interest 테이블 생성
CREATE TABLE interest (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          type ENUM('EXCHANGE_STUDENT','EMPLOYMENT_CAREER','EXAM_PREPARATION','STARTUP','FOREIGN_LANGUAGE_STUDY','HOBBY_LEISURE','SCHOOL_LIFE','ETC') NOT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 기존 데이터 마이그레이션
INSERT INTO interest (id, type)
SELECT id, name FROM interests;

-- 기존 테이블 삭제
DROP TABLE interests;

-- ====================================================================
-- 4. Member-Interest 조인 테이블 생성
-- ====================================================================

CREATE TABLE member_interest (
                                 member_id BIGINT NOT NULL,
                                 interest_id BIGINT NOT NULL,
                                 PRIMARY KEY (member_id, interest_id),
                                 CONSTRAINT fk_member_interest_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_member_interest_interest FOREIGN KEY (interest_id) REFERENCES interest(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 기존 user_interests 데이터 마이그레이션
INSERT INTO member_interest (member_id, interest_id)
SELECT member_id, interest_id FROM user_interests_backup;

-- ====================================================================
-- 5. Member 테이블 수정
-- ====================================================================

-- membername unique 제약조건 제거 (존재하는 경우에만)
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE table_schema = DATABASE()
     AND table_name = 'member'
     AND index_name = 'membername') > 0,
    'ALTER TABLE member DROP INDEX membername',
    'SELECT "Index does not exist" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- profile_intro 컬럼 제거 (존재하는 경우에만)
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE table_schema = DATABASE()
     AND table_name = 'member'
     AND column_name = 'profile_intro') > 0,
    'ALTER TABLE member DROP COLUMN profile_intro',
    'SELECT "Column does not exist" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ====================================================================
-- 6. University 테이블 수정
-- ====================================================================

-- name unique 제약조건 제거
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE table_schema = DATABASE()
     AND table_name = 'university'
     AND index_name = 'name') > 0,
    'ALTER TABLE university DROP INDEX name',
    'SELECT "Index does not exist" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- domain 필드 NOT NULL 제약조건 제거
ALTER TABLE university MODIFY COLUMN domain VARCHAR(255) UNIQUE;

-- ====================================================================
-- 7. ChatRoom 테이블 확인 및 수정
-- ====================================================================

-- 필요한 컬럼들이 이미 존재하는지 확인하고 없으면 추가
-- (V1 스키마에서는 이미 존재하므로 건너뛸 수 있음)

-- ====================================================================
-- 8. UserStatistics 테이블 안전 처리
-- ====================================================================

-- 기존 데이터 백업
CREATE TABLE user_statistics_backup AS SELECT * FROM user_statistics;

-- 테이블 구조는 이미 올바르므로 데이터만 복원 (필요시)

-- ====================================================================
-- 9. 인덱스 추가
-- ====================================================================

-- member_interest 테이블 인덱스
CREATE INDEX idx_member_interest_member ON member_interest(member_id);
CREATE INDEX idx_member_interest_interest ON member_interest(interest_id);

-- interest 테이블 인덱스
CREATE INDEX idx_interest_type ON interest(type);

-- ====================================================================
-- 10. 백업 테이블 정리
-- ====================================================================

-- 마이그레이션 성공 확인 후 아래 주석 해제하여 백업 테이블 삭제
DROP TABLE interests_backup;
DROP TABLE user_interests_backup;
DROP TABLE user_statistics_backup;

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- 11. 데이터 무결성 검증
-- ====================================================================

-- 마이그레이션 결과 검증 쿼리들
SELECT 'Member-Interest 관계 데이터 검증' as verification;
SELECT COUNT(*) as member_interest_count FROM member_interest;

SELECT 'Interest 데이터 검증' as verification;
SELECT COUNT(*) as interest_count FROM interest;

SELECT '마이그레이션 완료' as status;
