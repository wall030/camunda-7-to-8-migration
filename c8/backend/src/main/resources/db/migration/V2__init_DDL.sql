create table users (
    id bigserial not null,
    username varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    first_name varchar(255),
    last_name varchar(255),
    constraint pk_users primary key (id),
    constraint uk_users_username unique (username),
    constraint uk_users_email unique (email)
);

create table groups (
    id bigserial not null,
    name varchar(255) not null,
    description text,
    constraint pk_groups primary key (id),
    constraint uk_groups_name unique (name)
);

create table user_groups (
    id bigserial not null,
    user_id bigint not null,
    group_id bigint not null,
    assigned_at timestamp default current_timestamp,
    constraint pk_user_groups primary key (id),
    constraint fk_user_groups_user foreign key (user_id) references users(id) on delete cascade,
    constraint fk_user_groups_group foreign key (group_id) references groups(id) on delete cascade,
    constraint uk_user_groups_user_group unique (user_id, group_id)
);

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
    student_id bigint not null,
    course_id bigint not null,
    constraint pk_student_course primary key (student_id, course_id),
    constraint fk_sc_course foreign key (course_id) references course(id) on delete cascade
    );

create table justifications (
    id uuid not null,
    student_id bigint not null,
    justification text not null,
    created_at timestamp not null default now(),
    constraint pk_justifications primary key (id),
    constraint fk_justifications_student foreign key (student_id) references users(id) on delete cascade
);


insert into users (username, email, password, first_name, last_name) values
    ('ca', 'cassian@mail.com', '$2a$10$.EFcvpFxBngR4hB5QViGn.rvfJttfU32lRQV1mAZDIfcYexsRLcNq', 'cassian', 'andor'),
    ('ba', 'bail@mail.com', '$2a$10$.EFcvpFxBngR4hB5QViGn.rvfJttfU32lRQV1mAZDIfcYexsRLcNq', 'bail', 'organa'),
    ('rt', 'rick@mail.com', '$2a$10$.EFcvpFxBngR4hB5QViGn.rvfJttfU32lRQV1mAZDIfcYexsRLcNq', 'rick', 'tecer'),
    ('demo', 'demo@mail.com', '$2a$10$.EFcvpFxBngR4hB5QViGn.rvfJttfU32lRQV1mAZDIfcYexsRLcNq', 'demo', 'demo');

insert into groups (name, description) values
    ('admin', 'admin'),
    ('students', 'students'),
    ('examoffice', 'examination office'),
    ('technicalservice', 'technical service');

insert into user_groups (user_id, group_id) values
    (1, 2),
    (2, 3),
    (3, 4),
    (4, 1);

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