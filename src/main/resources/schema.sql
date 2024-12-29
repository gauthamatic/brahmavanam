CREATE TABLE rrule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    freq VARCHAR(50) NOT NULL,
    interval_value INT NOT NULL,
    byweekday VARCHAR(255),
    dtstart VARCHAR(50) NOT NULL
);

CREATE TABLE event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    dtstart VARCHAR(50),
    dtend VARCHAR(50),
    rrule_id INT,
    color VARCHAR(50),
    text_color VARCHAR(50),
    CONSTRAINT fk_rrule FOREIGN KEY (rrule_id) REFERENCES rrule(id)
);

-- Insert into rrule table
INSERT INTO rrule (freq, interval_value, byweekday, dtstart)
VALUES ('weekly', 1, 'su', '2023-01-01T10:00:00');

select @rrule_id := scope_identity();

-- Get the last inserted id from rrule table
INSERT INTO event (title, rrule_id, color, text_color)
VALUES ('Open to All', @rrule_id , '#ffcccc', '#990000');