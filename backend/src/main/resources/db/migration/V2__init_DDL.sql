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

-- Add 'students' group
INSERT INTO ACT_ID_GROUP (ID_, NAME_, TYPE_)
VALUES ('students', 'students', 'WORKFLOW');

-- Add 'examination_office' group
INSERT INTO ACT_ID_GROUP (ID_, NAME_, TYPE_)
VALUES ('examination_office', 'examination_office', 'WORKFLOW');


-- student
INSERT INTO act_id_user (ID_, FIRST_ , LAST_, EMAIL_)
VALUES ('cassian_andor', 'Cassian', 'Andor', 'cassian@mail.com');

-- examination office
INSERT INTO act_id_user (ID_, FIRST_ , LAST_, EMAIL_)
VALUES ('bail_organa', 'Bail', 'Organa', 'bail@mail.com');

-- course
INSERT INTO course (name)
VALUES ('Physics');

-- Assign Cassian to 'students' group
INSERT INTO ACT_ID_MEMBERSHIP (USER_ID_, GROUP_ID_)
VALUES ('cassian_andor', 'students');

-- Assign Bail to 'examination_office' group
INSERT INTO ACT_ID_MEMBERSHIP (USER_ID_, GROUP_ID_)
VALUES ('bail_organa', 'examination_office');
