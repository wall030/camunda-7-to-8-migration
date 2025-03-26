CREATE TABLE IF NOT EXISTS course (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_course_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student_course (
    student_id VARCHAR(64) NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT pk_student_course PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_student_course_student FOREIGN KEY (student_id) REFERENCES act_id_user(id_) ON DELETE CASCADE,
    CONSTRAINT fk_student_course_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

CREATE TABLE justifications (
    id UUID PRIMARY KEY,
    student_id VARCHAR(64) NOT NULL,
    justification TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES act_id_user(id_) ON DELETE CASCADE
);

-- Add 'students' group
INSERT INTO ACT_ID_GROUP (ID_, NAME_, TYPE_)
VALUES ('students', 'students', 'WORKFLOW');

-- Add 'examOffice' group
INSERT INTO ACT_ID_GROUP (ID_, NAME_, TYPE_)
VALUES ('examOffice', 'examOffice', 'WORKFLOW');

-- Add 'technicalService' group
INSERT INTO ACT_ID_GROUP (ID_, NAME_, TYPE_)
VALUES ('technicalService', 'technicalService', 'WORKFLOW');


-- student
INSERT INTO act_id_user (ID_, REV_, PWD_, SALT_, FIRST_, LAST_, EMAIL_)
VALUES ('ca', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==','Cassian', 'Andor', 'cassian@mail.com');

-- examination office
INSERT INTO act_id_user (ID_, REV_, PWD_, SALT_, FIRST_ , LAST_, EMAIL_)
VALUES ('ba', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Bail', 'Organa', 'bail@mail.com');


-- techinal service
INSERT INTO act_id_user (ID_, REV_, PWD_, SALT_, FIRST_ , LAST_, EMAIL_)
VALUES ('rt', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Rick', 'Tecer', 'rick@mail.com');


-- course
INSERT INTO course (name)
VALUES ('Course A'),
        ('Course B');

-- Assign Cassian to 'students' group
INSERT INTO ACT_ID_MEMBERSHIP (USER_ID_, GROUP_ID_)
VALUES ('ca', 'students');

-- Assign Bail to 'examination_office' group
INSERT INTO ACT_ID_MEMBERSHIP (USER_ID_, GROUP_ID_)
VALUES ('ba', 'examOffice');

-- Assign Rick to 'technicalService' group
INSERT INTO ACT_ID_MEMBERSHIP (USER_ID_, GROUP_ID_)
VALUES ('rt', 'technicalService');