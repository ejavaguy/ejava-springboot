----
-- initial DB schema
----
create table vote (
  id varchar(50) not null,
  choice varchar(40),
  date timestamp,
  source varchar(40),
  constraint vote_pkey primary key(id)
);

comment on table vote is 'countable votes for election';