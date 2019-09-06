----
-- migrating schema to add new column
----

alter table reposongs_song add column artist varchar(255);

comment on column reposongs_song.artist is 'who recorded song';
