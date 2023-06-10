create table if not exists course
(
    id       int auto_increment primary key,
    name     varchar(50),
    capacity int check (capacity > 0)
);

create table if not exists student
(
    id   int auto_increment primary key,
    name varchar(50)
);

create table if not exists student_course
(
    id         int auto_increment primary key,
    course_id  int,
    student_id int,
    unique (course_id, student_id)
);