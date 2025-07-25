-- V2: Update schema to match current entities
-- Date: 2025-07-25

SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 기존 테이블들 삭제 (현재 엔티티에 없는 테이블들)
-- ====================================================================

DROP TABLE IF EXISTS chat_participant;
DROP TABLE IF EXISTS user_interests;

-- ====================================================================
-- 2. Interest 테이블 구조 완전 변경
-- ====================================================================

-- 기존 interests 테이블 삭제하고 새로 생성
DROP TABLE IF EXISTS interests;

CREATE TABLE interest (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type ENUM('EXCHANGE_STUDENT','EMPLOYMENT_CAREER','EXAM_PREPARATION','STARTUP','FOREIGN_LANGUAGE_STUDY','HOBBY_LEISURE','SCHOOL_LIFE','ETC') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 3. Member와 Interest 간 ManyToMany 조인 테이블 생성
-- ====================================================================

CREATE TABLE member_interest (
    member_id BIGINT NOT NULL,
    interest_id BIGINT NOT NULL,
    PRIMARY KEY (member_id, interest_id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (interest_id) REFERENCES interest(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 4. Member 테이블 수정
-- ====================================================================

-- profileIntro 필드 제거 (현재 엔티티에 없음)
ALTER TABLE member DROP COLUMN IF EXISTS profile_intro;

-- membername unique 제약조건 제거 (엔티티에서 unique=false)
ALTER TABLE member DROP INDEX IF EXISTS membername;

-- ====================================================================
-- 5. University 테이블 수정
-- ====================================================================

-- name unique 제약조건 제거 (엔티티에 unique 없음)
ALTER TABLE university DROP INDEX IF EXISTS name;

-- ====================================================================
-- 6. ChatRoom 테이블에 필드 추가 (현재 엔티티에 있는 필드들)
-- ====================================================================

ALTER TABLE chat_room 
ADD COLUMN IF NOT EXISTS starter_id BIGINT,
ADD COLUMN IF NOT EXISTS friend_id BIGINT,
ADD COLUMN IF NOT EXISTS is_exited_by_starter BIT(1) DEFAULT 0,
ADD COLUMN IF NOT EXISTS is_exited_by_friend BIT(1) DEFAULT 0,
ADD COLUMN IF NOT EXISTS last_message_send_at DATETIME(6);

-- 외래키 제약조건 추가
ALTER TABLE chat_room 
ADD CONSTRAINT IF NOT EXISTS fk_chat_room_starter 
FOREIGN KEY (starter_id) REFERENCES member(id);

ALTER TABLE chat_room 
ADD CONSTRAINT IF NOT EXISTS fk_chat_room_friend 
FOREIGN KEY (friend_id) REFERENCES member(id);

-- ====================================================================
-- 7. UserKeyword 테이블 ID 타입 변경
-- ====================================================================

-- 외래키 제약조건 임시 제거
ALTER TABLE user_keyword DROP FOREIGN KEY user_keyword_ibfk_1;

-- ID 타입을 BIGINT로 변경
ALTER TABLE user_keyword 
MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

-- 외래키 제약조건 다시 추가
ALTER TABLE user_keyword 
ADD CONSTRAINT user_keyword_ibfk_1 
FOREIGN KEY (member_id) REFERENCES member(id);

-- ====================================================================
-- 8. Matches 테이블 수정
-- ====================================================================

-- ID 타입을 BIGINT로 변경 (현재 엔티티에서 Long 타입)
ALTER TABLE matches 
MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

-- MatchStatus enum에서 REJECTED 제거 (현재 엔티티에 없음)
ALTER TABLE matches 
MODIFY COLUMN status ENUM('REQUESTED','ACCEPTED');

-- ====================================================================
-- 9. UserStatistics 테이블 재생성
-- ====================================================================

CREATE TABLE user_statistics (
    member_id BIGINT NOT NULL,
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    updated_at DATETIME(6),
    match_completed_count INT NOT NULL DEFAULT 0,
    match_received_count INT NOT NULL DEFAULT 0,
    match_requested_count INT NOT NULL DEFAULT 0,
    PRIMARY KEY (member_id),
    FOREIGN KEY (member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 10. 인덱스 업데이트
-- ====================================================================

-- 새로운 조인 테이블 인덱스
CREATE INDEX idx_member_interest_member ON member_interest(member_id);
CREATE INDEX idx_member_interest_interest ON member_interest(interest_id);

-- ChatRoom 새 필드 인덱스
CREATE INDEX idx_chat_room_starter ON chat_room(starter_id);
CREATE INDEX idx_chat_room_friend ON chat_room(friend_id);

-- UserStatistics 인덱스
CREATE INDEX idx_user_statistics_counts ON user_statistics(match_requested_count, match_received_count, match_completed_count);

-- Interest 테이블 인덱스
CREATE INDEX idx_interest_type ON interest(type);

SET FOREIGN_KEY_CHECKS = 1;
