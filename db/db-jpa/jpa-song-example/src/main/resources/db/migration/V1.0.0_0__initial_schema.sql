----
-- initial DB schema
----
drop sequence IF EXISTS hibernate_sequence;
drop table IF EXISTS reposongs_song;

create sequence hibernate_sequence start 1 increment 1;

create table reposongs_song (
    id int not null,
    title varchar(255),
    --artist varchar(255),
    released date,
    constraint song_pk primary key (id)
);

comment on table reposongs_song is 'song database';
comment on column reposongs_song.id is 'song primary key';
comment on column reposongs_song.title is 'official song name';
--comment on column reposongs_song.artist is 'who recorded song';
comment on column reposongs_song.released is 'date song released';