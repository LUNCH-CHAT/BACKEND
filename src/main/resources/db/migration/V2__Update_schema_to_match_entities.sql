-- V2__Update_schema_to_match_entities.sql
-- Author: 최민수
-- Date: 2025-07-26

SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. Member 테이블 수정 (membername UNIQUE 제거)
-- ====================================================================

ALTER TABLE member MODIFY COLUMN membername VARCHAR(255) NOT NULL;

-- ====================================================================
-- 2. University 테이블 수정 (name UNIQUE 제거, domain UNIQUE 유지 + 중복 인덱스 정리)
-- ====================================================================

ALTER TABLE university
    MODIFY COLUMN name VARCHAR(255) NOT NULL,
    MODIFY COLUMN domain VARCHAR(255) UNIQUE;

-- ====================================================================
-- 3. Boolean 필드 타입 변경 (TINYINT(1)로)
-- ====================================================================

ALTER TABLE chat_room
    MODIFY COLUMN is_exited_by_starter TINYINT(1) NOT NULL DEFAULT 0,
    MODIFY COLUMN is_exited_by_friend TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE chat_message
    MODIFY COLUMN is_read TINYINT(1) DEFAULT 0;

ALTER TABLE notification
    MODIFY COLUMN is_read TINYINT(1) DEFAULT 0;

-- ====================================================================
-- 4. Interest 관련 테이블 재구성 (테이블 없음/중복 방지)
-- ====================================================================

-- 기존 테이블 삭제 (안전하게 IF EXISTS)
DROP TABLE IF EXISTS user_interests;
DROP TABLE IF EXISTS interests;

-- 새로운 interest 테이블 생성 (IF NOT EXISTS 추가)
CREATE TABLE IF NOT EXISTS interest (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        type ENUM('EXCHANGE_STUDENT','EMPLOYMENT_CAREER','EXAM_PREPARATION','STARTUP',
                                        'FOREIGN_LANGUAGE_STUDY','HOBBY_LEISURE','SCHOOL_LIFE','ETC') NOT NULL,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- member_interest 조인 테이블 생성 (IF NOT EXISTS 추가)
CREATE TABLE IF NOT EXISTS member_interest (
                                               member_id BIGINT NOT NULL,
                                               interest_id BIGINT NOT NULL,
                                               PRIMARY KEY (member_id, interest_id),
    CONSTRAINT fk_member_interest_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_member_interest_interest FOREIGN KEY (interest_id) REFERENCES interest(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 5. 불필요한 테이블 삭제
-- ====================================================================

DROP TABLE IF EXISTS chat_participant;

-- ====================================================================
-- 6. 인덱스 생성
-- ====================================================================

CREATE INDEX IF NOT EXISTS idx_member_interest_member ON member_interest(member_id);
CREATE INDEX IF NOT EXISTS idx_member_interest_interest ON member_interest(interest_id);
CREATE INDEX IF NOT EXISTS idx_interest_type ON interest(type);

-- ====================================================================
-- 7. 백업 테이블 정리 (로그에 남아 있는 부분 제거)
-- ====================================================================

DROP TABLE IF EXISTS interests_backup;
DROP TABLE IF EXISTS user_interests_backup;

SET FOREIGN_KEY_CHECKS = 1;
