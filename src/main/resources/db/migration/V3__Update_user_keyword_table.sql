-- ============================================
-- LunchChat V3 Database Migration Script
-- UserKeyword 테이블 title 컬럼 nullable로 변경
-- ============================================

ALTER TABLE user_keyword MODIFY COLUMN title VARCHAR(100) NULL COMMENT '키워드 제목';

SELECT 'V3 Migration for user_keyword table completed successfully' as migration_result;
