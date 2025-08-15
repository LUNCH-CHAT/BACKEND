-- ============================================
-- LunchChat V6 Database Migration Script  
-- University ↔ College 엔티티 매핑 구조 수정
-- 회원의 소속 학교 기준으로 단과대 목록 조회하도록 로직 변경
-- ============================================

-- ============================================
-- 1. 기존 데이터 백업 및 분석
-- ============================================

-- 외래키 제약조건 임시 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 트랜잭션 시작
START TRANSACTION;

-- ============================================
-- 2. College 테이블 구조 변경
-- ============================================

-- university_id 컬럼이 존재하는지 확인
SET @university_id_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = DATABASE() 
    AND table_name = 'college' 
    AND column_name = 'university_id'
);

-- university_id 컬럼이 없다면 추가 (NULL 허용으로 먼저 추가)
SET @sql = IF(@university_id_exists = 0, 
    'ALTER TABLE college ADD COLUMN university_id BIGINT NULL COMMENT "대학교 ID (FK)" AFTER name;', 
    'SELECT "college.university_id column already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 2. College 테이블의 name 컬럼 unique 제약조건 제거
-- ============================================
-- 동일한 단과대명이 다른 대학에 존재할 수 있으므로 unique 제약조건 제거

-- 기존 unique 제약조건 확인
SET @unique_constraint_exists = (
    SELECT COUNT(*) 
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
    WHERE tc.table_schema = DATABASE() 
    AND tc.table_name = 'college' 
    AND tc.constraint_type = 'UNIQUE'
    AND kcu.column_name = 'name'
);

-- unique 제약조건이 있다면 제거
SET @sql = IF(@unique_constraint_exists > 0, 
    'ALTER TABLE college DROP INDEX name;', 
    'SELECT "college.name unique constraint does not exist" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- ============================================
-- 3. 기존 College 데이터에 university_id 설정 (주요 대학 기준)
-- ============================================

-- Member 데이터 분석을 통한 College-University 매핑
-- 각 College의 가장 많이 사용되는 University를 기준으로 설정

-- 1. 공학부 (가장 많이 사용: 가톨릭대 7명)
UPDATE college SET university_id = 1, updated_at = NOW() WHERE id = 1 AND name = '공학부';

-- 2. 자연과학부 (가장 많이 사용: 가톨릭대 2명)  
UPDATE college SET university_id = 1, updated_at = NOW() WHERE id = 2 AND name = '자연과학부';

-- 3. 경영경제학부 (가장 많이 사용: 가톨릭대 4명)
UPDATE college SET university_id = 1, updated_at = NOW() WHERE id = 3 AND name = '경영경제학부';

-- 4. 항공우주공학대학 (가장 많이 사용: 한국항공대 4명)
UPDATE college SET university_id = 2, updated_at = NOW() WHERE id = 4 AND name = '항공우주공학대학';

-- 5. 소프트웨어학부 (가장 많이 사용: UMC대 3명, 한국항공대 2명 → UMC대 선택)
UPDATE college SET university_id = 5, updated_at = NOW() WHERE id = 5 AND name = '소프트웨어학부';

-- 6. 경영대학 (가장 많이 사용: 한국항공대 2명)
UPDATE college SET university_id = 2, updated_at = NOW() WHERE id = 6 AND name = '경영대학';

-- ============================================
-- 4. 다른 University 소속 멤버들을 위한 추가 College 생성
-- ============================================

-- 현재 Max ID 확인
SET @max_college_id = (SELECT COALESCE(MAX(id), 0) FROM college);

-- 이화여대 ewha.ac.kr (university_id = 3)용 추가 단과대
INSERT INTO college (id, name, university_id, created_at, updated_at) VALUES
(@max_college_id + 1, '공학부', 3, NOW(), NOW()),
(@max_college_id + 2, '자연과학부', 3, NOW(), NOW()), 
(@max_college_id + 3, '경영경제학부', 3, NOW(), NOW());

SET @max_college_id = @max_college_id + 3;

-- 이화여대 ewhain.net (university_id = 4)용 추가 단과대  
INSERT INTO college (id, name, university_id, created_at, updated_at) VALUES
(@max_college_id + 1, '공학부', 4, NOW(), NOW()),
(@max_college_id + 2, '자연과학부', 4, NOW(), NOW()),
(@max_college_id + 3, '경영경제학부', 4, NOW(), NOW()),
(@max_college_id + 4, '소프트웨어학부', 4, NOW(), NOW());

SET @max_college_id = @max_college_id + 4;

-- UMC대 (university_id = 5)용 추가 단과대 (소프트웨어학부는 이미 할당됨)
INSERT INTO college (id, name, university_id, created_at, updated_at) VALUES
(@max_college_id + 1, '공학부', 5, NOW(), NOW()),
(@max_college_id + 2, '항공우주공학대학', 5, NOW(), NOW()),
(@max_college_id + 3, '경영대학', 5, NOW(), NOW());

-- 한국항공대 (university_id = 2)용 추가 단과대 (항공우주공학대학, 경영대학은 이미 할당됨)
INSERT INTO college (id, name, university_id, created_at, updated_at) VALUES
(@max_college_id + 4, '소프트웨어학부', 2, NOW(), NOW());

-- ============================================
-- 5. Member 데이터의 college_id 업데이트 (기존 College 유지하면서)
-- ============================================

-- 가톨릭대 멤버들: 기존 College (1,2,3) 그대로 유지 (이미 university_id=1로 설정됨)
-- 별도 업데이트 불필요

-- 한국항공대 멤버들: 기존 College 유지하되 소프트웨어학부만 새로 생성된 것으로 변경
UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 2 AND name = '소프트웨어학부' LIMIT 1
) WHERE university_id = 2 AND college_id = 5; -- 기존 소프트웨어학부(5) → 한국항공대 소프트웨어학부

-- 이화여대 ewha.ac.kr 멤버들: 새로 생성된 College로 변경
UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 3 AND name = '공학부' LIMIT 1
) WHERE university_id = 3 AND college_id = 1;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 3 AND name = '자연과학부' LIMIT 1  
) WHERE university_id = 3 AND college_id = 2;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 3 AND name = '경영경제학부' LIMIT 1
) WHERE university_id = 3 AND college_id = 3;

-- 이화여대 ewhain.net 멤버들: 새로 생성된 College로 변경
UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 4 AND name = '공학부' LIMIT 1
) WHERE university_id = 4 AND college_id = 1;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 4 AND name = '자연과학부' LIMIT 1
) WHERE university_id = 4 AND college_id = 2;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 4 AND name = '경영경제학부' LIMIT 1
) WHERE university_id = 4 AND college_id = 3;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 4 AND name = '소프트웨어학부' LIMIT 1
) WHERE university_id = 4 AND college_id = 5;

-- UMC대 멤버들: 기존 소프트웨어학부(5) 유지, 나머지는 새로 생성된 College로 변경
UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 5 AND name = '공학부' LIMIT 1
) WHERE university_id = 5 AND college_id = 1;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 5 AND name = '항공우주공학대학' LIMIT 1
) WHERE university_id = 5 AND college_id = 4;

UPDATE member SET college_id = (
    SELECT id FROM college WHERE university_id = 5 AND name = '경영대학' LIMIT 1
) WHERE university_id = 5 AND college_id = 6;

-- ============================================  
-- 6. Department 데이터는 기존 College와의 관계 유지 (변경 불필요)
-- ============================================

-- Department들은 기존 College ID와 그대로 연결 유지
-- 기존 College에 university_id가 추가되었으므로 별도 업데이트 불필요

-- Department 1,2: college_id=1 (가톨릭대 공학부) 유지
-- Department 3: college_id=2 (가톨릭대 자연과학부) 유지  
-- Department 4: college_id=3 (가톨릭대 경영경제학부) 유지
-- Department 5,6: college_id=4 (한국항공대 항공우주공학대학) 유지
-- Department 7: college_id=5 (UMC대 소프트웨어학부) 유지
-- Department 8: college_id=6 (한국항공대 경영대학) 유지

-- ============================================
-- 7. AUTO_INCREMENT 값 재설정
-- ============================================

-- College 테이블의 AUTO_INCREMENT 값을 올바르게 설정
SET @max_college_id_final = (SELECT COALESCE(MAX(id), 0) FROM college);
SET @sql = CONCAT('ALTER TABLE college AUTO_INCREMENT = ', @max_college_id_final + 1);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 8. 외래키 제약조건 및 제약조건 추가
-- ============================================

-- university_id 컬럼을 NOT NULL로 변경
ALTER TABLE college MODIFY COLUMN university_id BIGINT NOT NULL COMMENT '대학교 ID (FK)';

-- university 테이블 존재 여부 확인
SET @university_table_exists = (
    SELECT COUNT(*) 
    FROM information_schema.tables 
    WHERE table_schema = DATABASE() 
    AND table_name = 'university'
);

-- 기존 외래키 제약조건 존재 여부 확인
SET @fk_constraint_exists = (
    SELECT COUNT(*) 
    FROM information_schema.key_column_usage 
    WHERE table_schema = DATABASE() 
    AND table_name = 'college' 
    AND column_name = 'university_id' 
    AND constraint_name LIKE 'fk_%'
);

-- university 테이블이 존재하고 외래키 제약조건이 없다면 추가
SET @sql = IF(@university_table_exists > 0 AND @fk_constraint_exists = 0, 
    'ALTER TABLE college ADD CONSTRAINT fk_college_university FOREIGN KEY (university_id) REFERENCES university(id) ON DELETE CASCADE;', 
    'SELECT "college university_id foreign key already exists or university table not found" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 복합 unique 제약조건 존재 여부 확인
SET @composite_unique_exists = (
    SELECT COUNT(*) 
    FROM information_schema.table_constraints tc
    WHERE tc.table_schema = DATABASE() 
    AND tc.table_name = 'college' 
    AND tc.constraint_type = 'UNIQUE'
    AND tc.constraint_name LIKE '%university_name%'
);

-- 복합 unique 제약조건이 없다면 추가
SET @sql = IF(@composite_unique_exists = 0, 
    'ALTER TABLE college ADD CONSTRAINT uk_college_university_name UNIQUE (university_id, name);', 
    'SELECT "college composite unique constraint already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- university_id 인덱스 추가 (없는 경우에만)
SET @university_index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'college' 
    AND column_name = 'university_id' 
    AND index_name = 'idx_college_university_id'
);

SET @sql = IF(@university_index_exists = 0, 
    'CREATE INDEX idx_college_university_id ON college(university_id);', 
    'SELECT "idx_college_university_id index already exists" as result;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 트랜잭션 커밋
COMMIT;

-- 외래키 제약조건 재활성화
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- Migration Complete
-- ============================================

SELECT 'V6 Migration for University ↔ College relationship completed successfully' as migration_result;