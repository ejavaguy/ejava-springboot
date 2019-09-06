--users-populate.sql
insert into users(username, password, enabled) values('sam','{noop}password',true);
insert into users(username, password, enabled) values('rebecca','{noop}password',true);
insert into users(username, password, enabled) values('woody','{noop}password',true);
insert into users(username, password, enabled) values('carla','{noop}password',true);
insert into users(username, password, enabled) values('norm','{noop}password',true);
insert into users(username, password, enabled) values('cliff','{noop}password',true);
insert into users(username, password, enabled) values('frasier','{noop}password',true);

insert into authorities(username, authority) values('sam','ROLE_ADMIN');
insert into authorities(username, authority) values('rebecca','ROLE_ADMIN');

insert into authorities(username, authority) values('woody','ROLE_CLERK');
insert into authorities(username, authority) values('carla','ROLE_CLERK');

insert into authorities(username, authority) values('norm','ROLE_CUSTOMER');
insert into authorities(username, authority) values('cliff','ROLE_CUSTOMER');
insert into authorities(username, authority) values('frasier','ROLE_CUSTOMER');
insert into authorities(username, authority) values('frasier','PRICE_CHECK');
