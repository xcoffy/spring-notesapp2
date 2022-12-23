drop table if exists notes;
create table notes(
                      id int primary key auto_increment,
                      title varchar(25) not null,
                      description varchar(100) not null
)
