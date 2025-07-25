-- V2__Update_schema_to_match_entities.sql
SET FOREIGN_KEY_CHECKS = 0;

/* ---------- 1. 관심사 데이터 백업 ---------- */
CREATE TABLE interests_backup AS SELECT * FROM interests;
CREATE TABLE user_interests_backup AS SELECT * FROM user_interests;

/* ---------- 2. 불필요 테이블 삭제 ---------- */
DROP TABLE IF EXISTS chat_participant;
DROP TABLE IF EXISTS user_interests;

/* ---------- 3. interest 테이블 재구성 ---------- */
CREATE TABLE interest (
                          id   BIGINT NOT NULL AUTO_INCREMENT,
                          type ENUM('EXCHANGE_STUDENT','EMPLOYMENT_CAREER','EXAM_PREPARATION','STARTUP',
            'FOREIGN_LANGUAGE_STUDY','HOBBY_LEISURE','SCHOOL_LIFE','ETC') NOT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO interest (id, type)
SELECT id, name FROM interests;

DROP TABLE interests;

/* ---------- 4. member_interest 조인 테이블 ---------- */
CREATE TABLE member_interest (
                                 member_id   BIGINT NOT NULL,
                                 interest_id BIGINT NOT NULL,
                                 PRIMARY KEY (member_id, interest_id),
                                 CONSTRAINT fk_member_interest_member   FOREIGN KEY (member_id)   REFERENCES member(id)   ON DELETE CASCADE,
                                 CONSTRAINT fk_member_interest_interest FOREIGN KEY (interest_id) REFERENCES interest(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO member_interest (member_id, interest_id)
SELECT member_id, interest_id FROM user_interests_backup;

/* ---------- 5. university 도메인 unique만 유지 ---------- */
ALTER TABLE university MODIFY COLUMN domain VARCHAR(255) UNIQUE;

/* ---------- 6. 인덱스 ---------- */
CREATE INDEX idx_member_interest_member   ON member_interest(member_id);
CREATE INDEX idx_member_interest_interest ON member_interest(interest_id);
CREATE INDEX idx_interest_type            ON interest(type);

/* ---------- 7. 백업 테이블 정리 ---------- */
DROP TABLE interests_backup;
DROP TABLE user_interests_backup;

SET FOREIGN_KEY_CHECKS = 1;
