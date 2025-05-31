insert into act_id_group (id_, name_, type_) values
    ('students', 'students', 'students'),
    ('examoffice', 'examoffice', 'examoffice'),
    ('technicalservice', 'technicalservice', 'technicalservice');



-- TEST DATA

insert into act_id_user (id_, rev_, pwd_, salt_, first_, last_, email_) values
    ('ca', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Cassian', 'Andor', 'cassian@mail.com'),
    ('ba', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Bail', 'Organa', 'bail@mail.com'),
    ('rt', 1, '{SHA-512}lrYuJ6QfWlyrcS4JL5uV6XdyXVfcf+sUBdGtySK20RBorvTCjEB3GMAfnScgDMzKXNQyMf61XIe2Zcqvnil8bA==', 'JaKB6RMHFxeryKNnes90dg==', 'Rick', 'Tecer', 'rick@mail.com');


-- cassian → studenten
insert into act_id_membership (user_id_, group_id_) values ('ca', 'students');

-- bail → prüfungsamt
insert into act_id_membership (user_id_, group_id_) values ('ba', 'examoffice');

-- rick → technischer dienst
insert into act_id_membership (user_id_, group_id_) values ('rt', 'technicalservice');
