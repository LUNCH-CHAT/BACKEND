-- ============================================
-- LunchChat V1 Database Schema Creation Script
-- ============================================

-- 의존성 순서에 따른 테이블 생성 및 마이그레이션 고려사항:
-- 1. 기본 참조 테이블들 먼저 생성
-- 2. 외래키 제약조건은 별도 섹션에서 관리
-- 3. 인덱스는 성능 최적화 섹션에서 별도 관리

-- ============================================
-- 1. 기본 참조 테이블들 (Dependencies가 없는 테이블들)
-- ============================================

-- 단과대 테이블
CREATE TABLE college (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL UNIQUE COMMENT '단과대학명',
                         created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                         updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                         deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='단과대학 테이블';

-- 대학교 테이블
CREATE TABLE university (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL COMMENT '대학교명',
                            domain VARCHAR(100) UNIQUE COMMENT '이메일 도메인 (예: ewha.ac.kr)',
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                            deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='대학교 테이블';

-- 학과 테이블
CREATE TABLE department (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL COMMENT '학과명',
                            college_id BIGINT NULL COMMENT '단과대학 ID',
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                            deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='학과 테이블';

-- 관심사 테이블
CREATE TABLE interest (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          type VARCHAR(50) NOT NULL COMMENT '관심사 타입 (EXCHANGE_STUDENT, EMPLOYMENT_CAREER, etc.)',
                          created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                          updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                          deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='관심사 테이블';

-- 프로젝트 정보 테이블
CREATE TABLE project_info (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              title VARCHAR(200) NULL COMMENT '프로젝트 제목',
                              content TEXT NULL COMMENT '프로젝트 내용',
                              created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                              updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                              deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='프로젝트 정보 테이블';

-- FAQ 테이블
CREATE TABLE faq (
                     id INT AUTO_INCREMENT PRIMARY KEY,
                     question TEXT NULL COMMENT '질문',
                     answer TEXT NULL COMMENT '답변',
                     created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                     updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                     deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FAQ 테이블';

-- ============================================
-- 2. 핵심 엔티티 테이블들
-- ============================================

-- 멤버 테이블 (핵심 테이블)
CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        membername VARCHAR(100) NOT NULL COMMENT '실명',
                        email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일',
                        nickname VARCHAR(50) NULL COMMENT '닉네임',
                        password VARCHAR(255) NOT NULL COMMENT '비밀번호',
                        role VARCHAR(20) NOT NULL COMMENT '권한',
                        student_no VARCHAR(20) NULL COMMENT '학번',
                        profile_image_url VARCHAR(500) NULL COMMENT '프로필 이미지 URL',
                        fcm_token VARCHAR(500) NULL COMMENT 'FCM 토큰',
                        login_type VARCHAR(20) NOT NULL COMMENT '로그인 타입 (LChat, Google)',
                        status VARCHAR(20) NULL COMMENT '멤버 상태 (ACTIVE, INACTIVE, PENDING)',
                        university_id BIGINT NULL COMMENT '대학교 ID',
                        college_id BIGINT NULL COMMENT '단과대학 ID',
                        department_id BIGINT NULL COMMENT '학과 ID',
                        created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                        updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                        deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='멤버 테이블';

-- ============================================
-- 3. 관계 테이블들 (Member에 의존하는 테이블들)
-- ============================================

-- 사용자 통계 테이블
CREATE TABLE user_statistics (
                                 member_id BIGINT PRIMARY KEY COMMENT '멤버 ID (FK)',
                                 match_requested_count INT NOT NULL DEFAULT 0 COMMENT '매칭 요청 수',
                                 match_received_count INT NOT NULL DEFAULT 0 COMMENT '매칭 받은 수',
                                 match_completed_count INT NOT NULL DEFAULT 0 COMMENT '매칭 완료 수',
                                 created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                                 updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                                 deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자 통계 테이블';

-- 사용자 키워드 테이블
CREATE TABLE user_keyword (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              member_id BIGINT NULL COMMENT '멤버 ID',
                              type VARCHAR(20) NOT NULL COMMENT '키워드 타입 (EXPRESS, GOAL, INTEREST)',
                              title VARCHAR(100) NOT NULL COMMENT '키워드 제목',
                              description TEXT NULL COMMENT '키워드 설명',
                              created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                              updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                              deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자 키워드 테이블';

-- 시간표 테이블
CREATE TABLE time_table (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            member_id BIGINT NULL COMMENT '멤버 ID',
                            day_of_week VARCHAR(3) NOT NULL COMMENT '요일 (MON, TUE, WED, THU, FRI, SAT, SUN)',
                            start_time TIME NULL COMMENT '시작 시간',
                            end_time TIME NULL COMMENT '종료 시간',
                            subject_name VARCHAR(100) NULL COMMENT '과목명',
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                            deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='시간표 테이블';

-- 알림 테이블
CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NULL COMMENT '멤버 ID',
                              type VARCHAR(50) NULL COMMENT '알림 타입',
                              content TEXT NULL COMMENT '알림 내용',
                              is_read BOOLEAN NULL DEFAULT FALSE COMMENT '읽음 여부',
                              created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                              updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                              deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='알림 테이블';

-- 매칭 테이블
CREATE TABLE matches (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         from_member_id BIGINT NULL COMMENT '매칭 요청자 ID',
                         to_member_id BIGINT NULL COMMENT '매칭 대상자 ID',
                         status VARCHAR(20) NULL COMMENT '매칭 상태 (REQUESTED, ACCEPTED)',
                         created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                         updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                         deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='매칭 테이블';

-- 채팅방 테이블
CREATE TABLE chat_room (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           starter_id BIGINT NULL COMMENT '채팅 시작자 ID',
                           friend_id BIGINT NULL COMMENT '채팅 상대방 ID',
                           is_exited_by_starter BOOLEAN NOT NULL DEFAULT FALSE COMMENT '시작자 나감 여부',
                           is_exited_by_friend BOOLEAN NOT NULL DEFAULT FALSE COMMENT '상대방 나감 여부',
                           last_message_send_at DATETIME(6) NULL COMMENT '마지막 메시지 전송 시간',
                           created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                           updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                           deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='채팅방 테이블';

-- 채팅 메시지 테이블
CREATE TABLE chat_message (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              chat_room_id BIGINT NULL COMMENT '채팅방 ID',
                              sender_id BIGINT NULL COMMENT '발신자 ID',
                              content TEXT NULL COMMENT '메시지 내용',
                              is_read BOOLEAN NULL DEFAULT FALSE COMMENT '읽음 여부',
                              sent_at DATETIME(6) NULL COMMENT '전송 시간',
                              created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
                              updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
                              deleted_at DATETIME(6) NULL COMMENT '삭제일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='채팅 메시지 테이블';

-- ============================================
-- 4. 다대다 관계 테이블
-- ============================================

-- 멤버-관심사 매핑 테이블
CREATE TABLE member_interest (
                                 member_id BIGINT NOT NULL COMMENT '멤버 ID',
                                 interest_id BIGINT NOT NULL COMMENT '관심사 ID',
                                 PRIMARY KEY (member_id, interest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='멤버-관심사 매핑 테이블';

-- ============================================
-- 5. 외래키 제약조건 (마이그레이션 시 별도 관리 가능)
-- ============================================

-- Department 외래키
ALTER TABLE department ADD CONSTRAINT fk_department_college
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE SET NULL;

-- Member 외래키들
ALTER TABLE member ADD CONSTRAINT fk_member_university
    FOREIGN KEY (university_id) REFERENCES university(id) ON DELETE SET NULL;

ALTER TABLE member ADD CONSTRAINT fk_member_college
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE SET NULL;

ALTER TABLE member ADD CONSTRAINT fk_member_department
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL;

-- UserStatistics 외래키
ALTER TABLE user_statistics ADD CONSTRAINT fk_user_statistics_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;

-- UserKeyword 외래키
ALTER TABLE user_keyword ADD CONSTRAINT fk_user_keyword_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;

-- TimeTable 외래키
ALTER TABLE time_table ADD CONSTRAINT fk_time_table_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;

-- Notification 외래키
ALTER TABLE notification ADD CONSTRAINT fk_notification_member
    FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE;

-- Matches 외래키들
ALTER TABLE matches ADD CONSTRAINT fk_matches_from_member
    FOREIGN KEY (from_member_id) REFERENCES member(id) ON DELETE CASCADE;

ALTER TABLE matches ADD CONSTRAINT fk_matches_to_member
    FOREIGN KEY (to_member_id) REFERENCES member(id) ON DELETE CASCADE;

-- ChatRoom 외래키들
ALTER TABLE chat_room ADD CONSTRAINT fk_chat_room_starter
    FOREIGN KEY (starter_id) REFERENCES member(id) ON DELETE CASCADE;

ALTER TABLE chat_room ADD CONSTRAINT fk_chat_room_friend
    FOREIGN KEY (friend_id) REFERENCES member(id) ON DELETE CASCADE;

-- ChatMessage 외래키들
ALTER TABLE chat_message ADD CONSTRAINT fk_chat_message_chat_room
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id) ON DELETE CASCADE;

ALTER TABLE chat_message ADD CONSTRAINT fk_chat_message_sender
    FOREIGN KEY (sender_id) REFERENCES member(id) ON DELETE CASCADE;

-- MemberInterest 외래키들
ALTER TABLE member_interest ADD CONSTRAINT fk_member_interest_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;

ALTER TABLE member_interest ADD CONSTRAINT fk_member_interest_interest
    FOREIGN KEY (interest_id) REFERENCES interest(id) ON DELETE CASCADE;

-- ============================================
-- 6. 성능 최적화를 위한 인덱스 (마이그레이션 시 별도 관리 가능)
-- ============================================

-- Member 테이블 인덱스
CREATE INDEX idx_member_email ON member(email);
CREATE INDEX idx_member_status ON member(status);
CREATE INDEX idx_member_university ON member(university_id);
CREATE INDEX idx_member_college ON member(college_id);
CREATE INDEX idx_member_department ON member(department_id);

-- 관계 테이블들 인덱스
CREATE INDEX idx_user_keyword_member ON user_keyword(member_id);
CREATE INDEX idx_time_table_member ON time_table(member_id);
CREATE INDEX idx_time_table_day ON time_table(day_of_week);
CREATE INDEX idx_notification_member ON notification(user_id);
CREATE INDEX idx_notification_read ON notification(is_read);
CREATE INDEX idx_matches_from_member ON matches(from_member_id);
CREATE INDEX idx_matches_to_member ON matches(to_member_id);
CREATE INDEX idx_matches_status ON matches(status);
CREATE INDEX idx_chat_room_starter ON chat_room(starter_id);
CREATE INDEX idx_chat_room_friend ON chat_room(friend_id);
CREATE INDEX idx_chat_message_chat_room ON chat_message(chat_room_id);
CREATE INDEX idx_chat_message_sender ON chat_message(sender_id);
CREATE INDEX idx_chat_message_sent_at ON chat_message(sent_at);

-- Soft Delete 지원을 위한 인덱스
CREATE INDEX idx_member_deleted_at ON member(deleted_at);
CREATE INDEX idx_college_deleted_at ON college(deleted_at);
CREATE INDEX idx_university_deleted_at ON university(deleted_at);
CREATE INDEX idx_department_deleted_at ON department(deleted_at);

-- ============================================
-- 7. 기본 데이터 삽입 (선택적)
-- ============================================

-- 관심사 기본 데이터
INSERT INTO interest (type) VALUES
                                ('EXCHANGE_STUDENT'),
                                ('EMPLOYMENT_CAREER'),
                                ('EXAM_PREPARATION'),
                                ('STARTUP'),
                                ('FOREIGN_LANGUAGE_STUDY'),
                                ('HOBBY_LEISURE'),
                                ('SCHOOL_LIFE'),
                                ('ETC');
