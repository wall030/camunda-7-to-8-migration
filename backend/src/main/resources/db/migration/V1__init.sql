CREATE TABLE IF NOT EXISTS student (
    id BIGSERIAL NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_student_id PRIMARY KEY (id),
    CONSTRAINT uq_student_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS course (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_course_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student_course (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT pk_student_course PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_student_course_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_course_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);
