create table if not exists prerequisites (
    id bigserial primary key,
    name varchar(100) not null
    );

insert into prerequisites (name) values
    ('prerequisite a'),
    ('prerequisite b'),
    ('prerequisite c'),
    ('prerequisite d');

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
    constraint fk_sc_student foreign key (student_id) references act_id_user(id_) on delete cascade,
    constraint fk_sc_course foreign key (course_id) references course(id) on delete cascade
    );

create table if not exists student_prerequisite (
    student_id varchar(64) not null,
    prerequisite_id bigint not null,
    constraint pk_student_prerequisite primary key (student_id, prerequisite_id),
    constraint fk_sp_student foreign key (student_id) references act_id_user(id_) on delete cascade,
    constraint fk_sp_prerequisite foreign key (prerequisite_id) references prerequisites(id) on delete cascade
    );

create table justifications (
    id uuid primary key,
    student_id varchar(64) not null,
    justification text not null,
    created_at timestamp not null default now(),
    constraint fk_student foreign key (student_id) references act_id_user(id_) on delete cascade
);

insert into act_id_group (id_, name_, type_) values
    ('students', 'students', 'students'),
    ('examoffice', 'examoffice', 'examoffice'),
    ('technicalservice', 'technicalservice', 'technicalservice');

insert into act_id_user (id_, rev_, pwd_, salt_, first_, last_, email_) values
    ('ca', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Cassian', 'Andor', 'cassian@mail.com'),
    ('ba', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Bail', 'Organa', 'bail@mail.com'),
    ('rt', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Rick', 'Tecer', 'rick@mail.com');

-- kurs-voraussetzungen
insert into course (name, current_size) values
    ('Course A', 0),
    ('Course B', 10);

insert into course_prerequisite (course_id, prerequisite_id) values
    (1, 1),
    (1, 2),
    (2, 3),
    (2, 4);

-- cassian → studenten
insert into act_id_membership (user_id_, group_id_) values ('ca', 'students');

-- bail → prüfungsamt
insert into act_id_membership (user_id_, group_id_) values ('ba', 'examoffice');

-- rick → technischer dienst
insert into act_id_membership (user_id_, group_id_) values ('rt', 'technicalservice');
