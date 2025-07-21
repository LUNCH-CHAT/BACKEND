-- LunchChat Database Initial Schema
-- Author: 최민수
-- Date: 2025-07-22

SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 기준 테이블들 (외래키 참조되는 테이블들)
-- ====================================================================

-- 대학교 테이블
CREATE TABLE university (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            domain VARCHAR(255) NOT NULL UNIQUE,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 단과대학 테이블
CREATE TABLE college (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL UNIQUE,
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 학과 테이블
CREATE TABLE department (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL,
                            college_id BIGINT,
                            PRIMARY KEY (id),
                            FOREIGN KEY (college_id) REFERENCES college(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 관심사 테이블
CREATE TABLE interests (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           name ENUM('EMPLOYMENT_CAREER','ETC','EXAM_PREPARATION','EXCHANGE_STUDENT','FOREIGN_LANGUAGE_STUDY','HOBBY_LEISURE','SCHOOL_LIFE','STARTUP') NOT NULL UNIQUE,
                           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 2. 메인 회원 테이블
-- ====================================================================

-- 회원 테이블
CREATE TABLE member (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at DATETIME(6),
                        deleted_at DATETIME(6),
                        updated_at DATETIME(6),
                        email VARCHAR(255) NOT NULL UNIQUE,
                        fcm_token VARCHAR(255),
                        login_type ENUM('Google','LChat') NOT NULL,
                        membername VARCHAR(255) NOT NULL UNIQUE,
                        profile_image_url VARCHAR(255),
                        student_no VARCHAR(255),
                        college_id BIGINT,
                        department_id BIGINT,
                        university_id BIGINT,
                        status ENUM('ACTIVE','INACTIVE','PENDING'),
                        nickname VARCHAR(255),
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (college_id) REFERENCES college(id),
                        FOREIGN KEY (department_id) REFERENCES department(id),
                        FOREIGN KEY (university_id) REFERENCES university(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 3. 채팅 관련 테이블들
-- ====================================================================

-- 채팅방 테이블
CREATE TABLE chat_room (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           created_at DATETIME(6),
                           deleted_at DATETIME(6),
                           updated_at DATETIME(6),
                           is_exited_by_friend BIT(1) NOT NULL,
                           is_exited_by_starter BIT(1) NOT NULL,
                           last_message_send_at DATETIME(6),
                           friend_id BIGINT,
                           starter_id BIGINT,
                           PRIMARY KEY (id),
                           FOREIGN KEY (friend_id) REFERENCES member(id),
                           FOREIGN KEY (starter_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 채팅 메시지 테이블
CREATE TABLE chat_message (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              content TEXT,
                              is_read BIT(1),
                              sent_at DATETIME(6),
                              chat_room_id BIGINT,
                              sender_id BIGINT,
                              created_at DATETIME(6),
                              deleted_at DATETIME(6),
                              updated_at DATETIME(6),
                              PRIMARY KEY (id),
                              FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
                              FOREIGN KEY (sender_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 채팅 참여자 테이블 (다대다 관계)
CREATE TABLE chat_participant (
                                  chat_room_id BIGINT NOT NULL,
                                  member_id BIGINT NOT NULL,
                                  PRIMARY KEY (chat_room_id, member_id),
                                  FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
                                  FOREIGN KEY (member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 4. 매칭 시스템 테이블
-- ====================================================================

-- 매칭 테이블
CREATE TABLE matches (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         created_at DATETIME(6),
                         status ENUM('ACCEPTED','REQUESTED'),
                         from_member_id BIGINT,
                         to_member_id BIGINT,
                         PRIMARY KEY (id),
                         FOREIGN KEY (from_member_id) REFERENCES member(id),
                         FOREIGN KEY (to_member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 5. 사용자 프로필 관련 테이블들
-- ====================================================================

-- 사용자 관심사 테이블 (다대다 관계)
CREATE TABLE user_interests (
                                interest_id BIGINT NOT NULL,
                                member_id BIGINT NOT NULL,
                                PRIMARY KEY (interest_id, member_id),
                                FOREIGN KEY (interest_id) REFERENCES interests(id),
                                FOREIGN KEY (member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 키워드 테이블
CREATE TABLE user_keyword (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              created_at DATETIME(6),
                              deleted_at DATETIME(6),
                              updated_at DATETIME(6),
                              description TEXT,
                              title VARCHAR(100) NOT NULL,
                              type ENUM('EXPRESS','GOAL','INTEREST') NOT NULL,
                              member_id BIGINT,
                              PRIMARY KEY (id),
                              FOREIGN KEY (member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 통계 테이블
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

-- 시간표 테이블
CREATE TABLE time_table (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            created_at DATETIME(6),
                            deleted_at DATETIME(6),
                            updated_at DATETIME(6),
                            day_of_week ENUM('FRI','MON','SAT','SUN','THU','TUE','WED') NOT NULL,
                            end_time TIME(6),
                            start_time TIME(6),
                            subject_name VARCHAR(255),
                            member_id BIGINT,
                            PRIMARY KEY (id),
                            FOREIGN KEY (member_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 6. 시스템 및 기타 테이블들
-- ====================================================================

-- 알림 테이블
CREATE TABLE notification (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              created_at DATETIME(6),
                              deleted_at DATETIME(6),
                              updated_at DATETIME(6),
                              content TEXT,
                              is_read BIT(1),
                              type VARCHAR(255),
                              user_id BIGINT,
                              PRIMARY KEY (id),
                              FOREIGN KEY (user_id) REFERENCES member(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- FAQ 테이블
CREATE TABLE faq (
                     id INT NOT NULL AUTO_INCREMENT,
                     answer TEXT,
                     question TEXT,
                     PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 프로젝트 정보 테이블
CREATE TABLE project_info (
                              id INT NOT NULL AUTO_INCREMENT,
                              content TEXT,
                              title VARCHAR(255),
                              PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- 7. 인덱스 생성 (성능 최적화)
-- ====================================================================

-- 자주 조회되는 컬럼들에 인덱스 추가
CREATE INDEX idx_member_email ON member(email);
CREATE INDEX idx_member_university ON member(university_id);
CREATE INDEX idx_member_college ON member(college_id);
CREATE INDEX idx_member_department ON member(department_id);
CREATE INDEX idx_member_status ON member(status);

CREATE INDEX idx_chat_message_room ON chat_message(chat_room_id);
CREATE INDEX idx_chat_message_sender ON chat_message(sender_id);
CREATE INDEX idx_chat_message_sent_at ON chat_message(sent_at);

CREATE INDEX idx_matches_from_member ON matches(from_member_id);
CREATE INDEX idx_matches_to_member ON matches(to_member_id);
CREATE INDEX idx_matches_status ON matches(status);

CREATE INDEX idx_notification_user ON notification(user_id);
CREATE INDEX idx_notification_read ON notification(is_read);

CREATE INDEX idx_time_table_member ON time_table(member_id);
CREATE INDEX idx_time_table_day ON time_table(day_of_week);

SET FOREIGN_KEY_CHECKS = 1;
