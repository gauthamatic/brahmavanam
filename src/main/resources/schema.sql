CREATE TABLE rrule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    freq VARCHAR(50) NOT NULL,
    interval_value INT NOT NULL,
    byweekday VARCHAR(255),
    dtstart DATE NOT NULL
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email_id VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    start_date DATE,
    end_date DATE,
    rrule_id INT,
    user_id INT,
    color VARCHAR(50),
    text_color VARCHAR(50),
    CONSTRAINT fk_rrule FOREIGN KEY (rrule_id) REFERENCES rrule(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert into rrule table
INSERT INTO rrule (freq, interval_value, byweekday, dtstart)
VALUES ('weekly', 1, 'su', '2023-01-01');

INSERT INTO event (title, rrule_id, color, text_color, start_date)
VALUES ('Open to All', LAST_INSERT_ID(), '#86f759', '#1b5d01', '2023-01-01');

INSERT INTO rrule (freq, interval_value, byweekday, dtstart)
VALUES ('weekly', 1, 'we,th', '2023-01-01');

INSERT INTO event (title, rrule_id, color, text_color, start_date)
VALUES ('No Bookings allowed', LAST_INSERT_ID(), '#eb8439', '#5d0101', '2023-01-01');

-- Insert into user table
INSERT INTO users (firstname, lastname, email_id, password)
VALUES ('John', 'Doe', 'john.doe@example.com', 'password123');

CREATE TABLE japa_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    malas INT NOT NULL,
    intensity VARCHAR(20) NOT NULL,
    duration_mins INT,
    logged_at DATETIME NOT NULL,
    notes TEXT,
    CONSTRAINT fk_japa_log_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE japa_target (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    target_malas INT NOT NULL,
    effective_from DATE NOT NULL,
    CONSTRAINT fk_japa_target_user FOREIGN KEY (user_id) REFERENCES users(id)
);
