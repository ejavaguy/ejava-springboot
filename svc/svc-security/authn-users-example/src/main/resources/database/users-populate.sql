--users-populate.sql
insert into users(username, password, enabled) values('user1','{noop}password',true);
insert into users(username, password, enabled) values('user2','{noop}password',true);
insert into users(username, password, enabled) values('user3','{noop}password',true);

insert into authorities(username, authority) values('user1','known');
insert into authorities(username, authority) values('user2','known');
insert into authorities(username, authority) values('user3','known');

update users
set password='{bcrypt}$2y$10$UvKwrln7xPp35c5sbj.9kuZ9jY9VYg/VylVTu88ZSCYy/YdcdP/Bq'
where username='user1';
update users
set password='{bcrypt}$2y$10$9tYKBY7act5dN.2d7kumuOsHytIJW8i23Ua2Qogcm6OM638IXMmLS'
where username='user2';
update users
set password='{bcrypt}$2y$10$AH6uepcNasVxlYeOhXX20.OX4cI3nXX.LsicoDE5G6bCP34URZZF2'
where username='user3';