create table if not exists prerequisites (
    id bigserial not null,
    name varchar(100) not null,
    constraint pk_prerequisite_id primary key (id)
    );

create table if not exists course (
    id bigserial not null,
    name varchar(255) not null,
    max_size int default 10,
    current_size int default 0,
    constraint pk_course_id primary key (id)
    );

create table if not exists course_prerequisite (
    course_id bigint not null,
    prerequisite_id bigint not null,
    constraint pk_course_prerequisite primary key (course_id, prerequisite_id),
    constraint fk_cp_course foreign key (course_id) references course(id) on delete cascade,
    constraint fk_cp_prerequisite foreign key (prerequisite_id) references prerequisites(id) on delete cascade
    );

create table if not exists student_course (
    student_id varchar(64) not null,
    course_id bigint not null,
    constraint pk_student_course primary key (student_id, course_id),
    constraint fk_sc_course foreign key (course_id) references course(id) on delete cascade
    );

create table if not exists student_prerequisite (
    student_id varchar(64) not null,
    prerequisite_id bigint not null,
    constraint pk_student_prerequisite primary key (student_id, prerequisite_id),
    constraint fk_sp_prerequisite foreign key (prerequisite_id) references prerequisites(id) on delete cascade
    );

create table justifications (
    id uuid primary key,
    student_id varchar(64) not null,
    justification text not null,
    created_at timestamp not null default now()
);

insert into prerequisites (name) values
    ('prerequisite a'),
    ('prerequisite b'),
    ('prerequisite c'),
    ('prerequisite d');

insert into course (name, current_size) values
    ('Course A', 0),
    ('Course B', 10);

insert into course_prerequisite (course_id, prerequisite_id) values
    (1, 1),
    (1, 2),
    (2, 3),
    (2, 4);