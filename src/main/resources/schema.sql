CREATE TABLE rrule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    freq VARCHAR(50) NOT NULL,
    interval_value INT NOT NULL,
    byweekday VARCHAR(255),
    start_date DATE NOT NULL
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
INSERT INTO rrule (freq, interval_value, byweekday, start_date)
VALUES ('weekly', 1, 'su', '2023-01-01');

select @rrule_id := scope_identity();

-- Get the last inserted id from rrule table
INSERT INTO event (title, rrule_id, color, text_color)
VALUES ('Open to All', @rrule_id , '#ffcccc', '#990000');

-- Insert into user table
INSERT INTO users (firstname, lastname, email_id, password)
VALUES ('John', 'Doe', 'john.doe@example.com', 'password123');