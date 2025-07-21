
-- V1__Create_initial_tables.sql

CREATE TABLE university (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE college (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    college_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES college(id)
);

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    membername VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    student_no VARCHAR(255),
    profile_image_url VARCHAR(255),
    fcm_token VARCHAR(255),
    login_type VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    university_id BIGINT,
    college_id BIGINT,
    department_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (university_id) REFERENCES university(id),
    FOREIGN KEY (college_id) REFERENCES college(id),
    FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    starter_id BIGINT,
    friend_id BIGINT,
    is_exited_by_starter BOOLEAN NOT NULL DEFAULT FALSE,
    is_exited_by_friend BOOLEAN NOT NULL DEFAULT FALSE,
    last_message_send_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (starter_id) REFERENCES member(id),
    FOREIGN KEY (friend_id) REFERENCES member(id)
);

CREATE TABLE chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT,
    sender_id BIGINT,
    content TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
    FOREIGN KEY (sender_id) REFERENCES member(id)
);

CREATE TABLE faq (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question TEXT,
    answer TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_member_id BIGINT,
    to_member_id BIGINT,
    status VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (from_member_id) REFERENCES member(id),
    FOREIGN KEY (to_member_id) REFERENCES member(id)
);

CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(255),
    content TEXT,
    is_read BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES member(id)
);

CREATE TABLE project_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE time_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    day_of_week VARCHAR(255) NOT NULL,
    start_time TIME,
    end_time TIME,
    subject_name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE user_interests (
    member_id BIGINT NOT NULL,
    interest_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (member_id, interest_id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (interest_id) REFERENCES interests(id)
);

CREATE TABLE user_keyword (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE user_statistics (
    member_id BIGINT PRIMARY KEY,
    match_requested_count INT DEFAULT 0 NOT NULL,
    match_received_count INT DEFAULT 0 NOT NULL,
    match_completed_count INT DEFAULT 0 NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);
