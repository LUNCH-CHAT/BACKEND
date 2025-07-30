-- ============================================
-- LunchChat V2 Database Migration Script
-- Notification 테이블 구조 완전 업데이트 (sender_id 포함)
-- ============================================

-- V1에서 V2로 마이그레이션하면서 notification 테이블 구조 변경 사항 적용
-- 실제 엔티티와 완벽하게 일치하도록 모든 필드 포함

-- ============================================
-- 1. Notification 테이블이 존재하지 않는 경우 생성
-- ============================================

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '알림 ID',
    user_id BIGINT NULL COMMENT '멤버 ID (FK) - 알림을 받는 사용자',
    sender_id BIGINT NULL COMMENT '발신자 ID (FK) - 알림을 보낸 사용자',
    type VARCHAR(50) NULL COMMENT '알림 타입',
    content TEXT NULL COMMENT '알림 내용',
    is_read BOOLEAN NULL DEFAULT FALSE COMMENT '읽음 여부',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='알림 테이블';

-- ============================================
-- 2. 기존 테이블 구조 확인 및 필요시 컬럼 추가/수정
-- ============================================

-- 2-1. id 컬럼이 BIGINT가 아닌 경우 변경
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'id' 
    AND data_type != 'bigint'
);

SET @sql = IF(@column_exists > 0, 
    'ALTER TABLE notification MODIFY COLUMN id BIGINT AUTO_INCREMENT;', 
    'SELECT "notification.id column already BIGINT" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2-2. user_id 컬럼 확인 및 추가 (member_id가 있다면 user_id로 변경)
SET @member_id_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'member_id'
);

SET @user_id_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'user_id'
);

-- member_id가 있고 user_id가 없다면 컬럼명 변경
SET @sql = IF(@member_id_exists > 0 AND @user_id_exists = 0, 
    'ALTER TABLE notification CHANGE COLUMN member_id user_id BIGINT NULL COMMENT "멤버 ID (FK) - 알림을 받는 사용자";', 
    'SELECT "notification.user_id column structure is correct" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- user_id 컬럼이 없다면 추가
SET @user_id_exists_after = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'user_id'
);

SET @sql = IF(@user_id_exists_after = 0, 
    'ALTER TABLE notification ADD COLUMN user_id BIGINT NULL COMMENT "멤버 ID (FK) - 알림을 받는 사용자" AFTER id;', 
    'SELECT "notification.user_id column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2-3. sender_id 컬럼 확인 및 추가 (핵심 추가 사항)
SET @sender_id_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'sender_id'
);

SET @sql = IF(@sender_id_exists = 0, 
    'ALTER TABLE notification ADD COLUMN sender_id BIGINT NULL COMMENT "발신자 ID (FK) - 알림을 보낸 사용자" AFTER user_id;', 
    'SELECT "notification.sender_id column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2-4. type 컬럼 확인 및 추가
SET @type_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'type'
);

SET @sql = IF(@type_exists = 0, 
    'ALTER TABLE notification ADD COLUMN type VARCHAR(50) NULL COMMENT "알림 타입" AFTER sender_id;', 
    'SELECT "notification.type column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2-5. content 컬럼 확인 및 TEXT 타입으로 설정
SET @content_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'content'
);

SET @content_is_text = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'content' 
    AND data_type = 'text'
);

-- content 컬럼이 없다면 추가
SET @sql = IF(@content_exists = 0, 
    'ALTER TABLE notification ADD COLUMN content TEXT NULL COMMENT "알림 내용" AFTER type;', 
    'SELECT "notification.content column exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- content 컬럼이 TEXT 타입이 아니라면 변경
SET @sql = IF(@content_exists > 0 AND @content_is_text = 0, 
    'ALTER TABLE notification MODIFY COLUMN content TEXT NULL COMMENT "알림 내용";', 
    'SELECT "notification.content column already TEXT type" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2-6. is_read 컬럼 확인 및 추가
SET @is_read_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'is_read'
);

SET @sql = IF(@is_read_exists = 0, 
    'ALTER TABLE notification ADD COLUMN is_read BOOLEAN NULL DEFAULT FALSE COMMENT "읽음 여부" AFTER content;', 
    'SELECT "notification.is_read column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2-7. BaseEntity 필드들 (created_at, updated_at, deleted_at) 확인 및 추가
SET @created_at_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'created_at'
);

SET @sql = IF(@created_at_exists = 0, 
    'ALTER TABLE notification ADD COLUMN created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT "생성일시" AFTER is_read;', 
    'SELECT "notification.created_at column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @updated_at_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'updated_at'
);

SET @sql = IF(@updated_at_exists = 0, 
    'ALTER TABLE notification ADD COLUMN updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT "수정일시" AFTER created_at;', 
    'SELECT "notification.updated_at column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @deleted_at_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'deleted_at'
);

SET @sql = IF(@deleted_at_exists = 0, 
    'ALTER TABLE notification ADD COLUMN deleted_at DATETIME(6) NULL COMMENT "삭제일시" AFTER updated_at;', 
    'SELECT "notification.deleted_at column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 3. 외래키 제약조건 추가 (member 테이블이 존재하는 경우)
-- ============================================

-- member 테이블 존재 여부 확인
SET @member_table_exists = (
    SELECT COUNT(*) 
    FROM information_schema.tables 
    WHERE table_schema = DATABASE() 
    AND table_name = 'member'
);

-- 기존 user_id 외래키 존재 여부 확인
SET @user_fk_exists = (
    SELECT COUNT(*) 
    FROM information_schema.key_column_usage 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'user_id' 
    AND constraint_name LIKE 'fk_%'
);

-- member 테이블이 존재하고 user_id 외래키가 없다면 추가
SET @sql = IF(@member_table_exists > 0 AND @user_fk_exists = 0, 
    'ALTER TABLE notification ADD CONSTRAINT fk_notification_member FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE;', 
    'SELECT "notification user_id foreign key already exists or member table not found" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 기존 sender_id 외래키 존재 여부 확인
SET @sender_fk_exists = (
    SELECT COUNT(*) 
    FROM information_schema.key_column_usage 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'sender_id' 
    AND constraint_name LIKE 'fk_%'
);

-- member 테이블이 존재하고 sender_id 외래키가 없다면 추가
SET @sql = IF(@member_table_exists > 0 AND @sender_fk_exists = 0, 
    'ALTER TABLE notification ADD CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES member(id) ON DELETE SET NULL;', 
    'SELECT "notification sender_id foreign key already exists or member table not found" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 4. 인덱스 추가 (성능 최적화)
-- ============================================

-- user_id 인덱스 추가 (없는 경우에만)
SET @user_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'user_id' 
    AND index_name = 'idx_notification_user_id'
);

SET @sql = IF(@user_index_exists = 0, 
    'CREATE INDEX idx_notification_user_id ON notification(user_id);', 
    'SELECT "idx_notification_user_id index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- sender_id 인덱스 추가 (없는 경우에만)
SET @sender_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'sender_id' 
    AND index_name = 'idx_notification_sender_id'
);

SET @sql = IF(@sender_index_exists = 0, 
    'CREATE INDEX idx_notification_sender_id ON notification(sender_id);', 
    'SELECT "idx_notification_sender_id index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- is_read 인덱스 추가 (없는 경우에만)
SET @read_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'is_read' 
    AND index_name = 'idx_notification_read'
);

SET @sql = IF(@read_index_exists = 0, 
    'CREATE INDEX idx_notification_read ON notification(is_read);', 
    'SELECT "idx_notification_read index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- type 인덱스 추가 (없는 경우에만)
SET @type_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'type' 
    AND index_name = 'idx_notification_type'
);

SET @sql = IF(@type_index_exists = 0, 
    'CREATE INDEX idx_notification_type ON notification(type);', 
    'SELECT "idx_notification_type index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- deleted_at 인덱스 추가 (Soft Delete 지원, 없는 경우에만)
SET @deleted_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND column_name = 'deleted_at' 
    AND index_name = 'idx_notification_deleted_at'
);

SET @sql = IF(@deleted_index_exists = 0, 
    'CREATE INDEX idx_notification_deleted_at ON notification(deleted_at);', 
    'SELECT "idx_notification_deleted_at index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 5. 복합 인덱스 추가 (성능 최적화)
-- ============================================

-- user_id + is_read 복합 인덱스 (알림 조회 성능 향상)
SET @composite_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND index_name = 'idx_notification_user_read'
);

SET @sql = IF(@composite_index_exists = 0, 
    'CREATE INDEX idx_notification_user_read ON notification(user_id, is_read);', 
    'SELECT "idx_notification_user_read composite index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- user_id + created_at 복합 인덱스 (시간순 정렬 조회 성능 향상)
SET @time_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND index_name = 'idx_notification_user_created'
);

SET @sql = IF(@time_index_exists = 0, 
    'CREATE INDEX idx_notification_user_created ON notification(user_id, created_at DESC);', 
    'SELECT "idx_notification_user_created composite index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- user_id + sender_id 복합 인덱스 (특정 사용자 간 알림 조회)
SET @user_sender_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'notification' 
    AND index_name = 'idx_notification_user_sender'
);

SET @sql = IF(@user_sender_index_exists = 0, 
    'CREATE INDEX idx_notification_user_sender ON notification(user_id, sender_id);', 
    'SELECT "idx_notification_user_sender composite index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- Migration Complete
-- ============================================

SELECT 'V2 Migration for notification table with sender_id completed successfully' as migration_result;
