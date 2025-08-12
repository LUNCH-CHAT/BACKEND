-- ============================================
-- InterestType enum 순서에 맞게 interest 테이블 데이터 수정
-- ============================================

-- 외래키 제약조건 임시 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 트랜잭션 시작
START TRANSACTION;

-- 1. 현재 interest 테이블의 id 값들을 변수에 저장
SET @foreign_language_id = (SELECT id FROM interest WHERE type = 'FOREIGN_LANGUAGE_STUDY');
SET @hobby_leisure_id = (SELECT id FROM interest WHERE type = 'HOBBY_LEISURE');
SET @school_life_id = (SELECT id FROM interest WHERE type = 'SCHOOL_LIFE');
SET @etc_id = (SELECT id FROM interest WHERE type = 'ETC');

-- 2. ETC 관련 데이터 삭제
DELETE FROM member_interest WHERE interest_id = @etc_id;
DELETE FROM interest WHERE type = 'ETC';

-- 3. 기존 데이터들을 안전한 임시 id로 이동
-- member_interest 참조 업데이트
UPDATE member_interest SET interest_id = 1005 WHERE interest_id = @foreign_language_id;
UPDATE member_interest SET interest_id = 1006 WHERE interest_id = @hobby_leisure_id;
UPDATE member_interest SET interest_id = 1007 WHERE interest_id = @school_life_id;

-- interest 테이블 id 임시 변경
UPDATE interest SET id = 1005 WHERE id = @foreign_language_id;
UPDATE interest SET id = 1006 WHERE id = @hobby_leisure_id;
UPDATE interest SET id = 1007 WHERE id = @school_life_id;

-- 4. GPA_MANAGEMENT 추가 (AUTO_INCREMENT 비활성화 후 수동 삽입)
ALTER TABLE interest AUTO_INCREMENT = 1;
INSERT INTO interest (id, type, created_at, updated_at) VALUES (5, 'GPA_MANAGEMENT', NOW(), NOW());

-- 5. 최종 id로 재정렬
UPDATE interest SET id = 6 WHERE id = 1005;  -- FOREIGN_LANGUAGE_STUDY
UPDATE interest SET id = 7 WHERE id = 1006;  -- HOBBY_LEISURE  
UPDATE interest SET id = 8 WHERE id = 1007;  -- SCHOOL_LIFE

-- 6. member_interest 테이블의 참조 id 최종 업데이트
UPDATE member_interest SET interest_id = 6 WHERE interest_id = 1005;
UPDATE member_interest SET interest_id = 7 WHERE interest_id = 1006;
UPDATE member_interest SET interest_id = 8 WHERE interest_id = 1007;

-- 7. AUTO_INCREMENT 값 재설정
ALTER TABLE interest AUTO_INCREMENT = 9;

-- 트랜잭션 커밋
COMMIT;

-- 외래키 제약조건 재활성화
SET FOREIGN_KEY_CHECKS = 1;