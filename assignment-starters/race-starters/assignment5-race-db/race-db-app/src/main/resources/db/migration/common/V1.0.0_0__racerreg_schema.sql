drop sequence IF EXISTS hibernate_sequence;
drop table IF EXISTS race_registration;

create sequence hibernate_sequence start 1 increment 1;
create table race_registration (
    id char(36) not null,
--    constraint registration_pk primary key (id)
);

comment on table race_registration is 'racer registrations';
comment on column race_registration.id is 'primary key';
--comment on column race_registration.race_name is 'name of race';
--comment on column race_registration.race_date is 'date of race';
--comment on column race_registration.first_name is 'first name of racer';
--comment on column race_registration.last_name is 'last name of racer';
--comment on column race_registration.age is 'age of racer on race date';
--comment on column race_registration.gender is 'gender of racer';
--comment on column race_registration.ownername is 'racer username';
