-- V3__Adjust_schema_for_latest_entities.sql
-- Author: 최민수
-- Date: 2025-07-26

SET FOREIGN_KEY_CHECKS = 0;

/* 1. university 테이블의 name 컬럼 UNIQUE 제약 제거 (엔티티에서 unique=false) */
ALTER TABLE university
    MODIFY COLUMN name VARCHAR(255) NOT NULL;

/* 2. Boolean 필드 타입을 JPA와 일치하도록 TINYINT(1)로 변경 (기존 BIT(1)에서 변경) */
/* chat_room 테이블 */
ALTER TABLE chat_room
    MODIFY COLUMN is_exited_by_starter TINYINT(1) NOT NULL DEFAULT 0,
    MODIFY COLUMN is_exited_by_friend TINYINT(1) NOT NULL DEFAULT 0;

/* chat_message 테이블 */
ALTER TABLE chat_message
    MODIFY COLUMN is_read TINYINT(1) DEFAULT 0;

/* notification 테이블 */
ALTER TABLE notification
    MODIFY COLUMN is_read TINYINT(1) DEFAULT 0;


DROP INDEX IF EXISTS name ON university;

SET FOREIGN_KEY_CHECKS = 1;
