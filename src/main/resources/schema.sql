use test;
create table course
(
    id   int auto_increment primary key,
    name varchar(50)
);

create table student
(
    id   int auto_increment primary key,
    name varchar(50)
);

create table student_course
(
    course_id  int,
    student_id int,
    foreign key (course_id) references course (id),
    foreign key (student_id) references student (id),
    primary key (course_id, student_id)
);