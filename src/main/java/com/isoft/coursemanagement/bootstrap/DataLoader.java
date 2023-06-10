package com.isoft.coursemanagement.bootstrap;

import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.services.CourseService;
import com.isoft.coursemanagement.services.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {
    private final CourseService courseService;
    private final StudentService studentService;

    public DataLoader(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @Override
    public void run(String... args) throws Exception {
        courseService.deleteAll().block();
        studentService.deleteAll().block();

        Course math = new Course("math", 10);
        Course physics = new Course("physics", 14);
        Course chemistry = new Course("chemistry", 6);


        Student ali = new Student("Ali");
        Student hossein = new Student("Hossein");
        Student hassan = new Student("Hassan");

        List<Course> courseList = List.of(math, physics, chemistry);
        List<Student> studentList = List.of(hossein, hassan, ali);

        Random random = new Random();
        for (int i = 0; i < 3; i++)
            for (int j = 1; j <= random.nextInt(4); j++) { // number of relations
                courseList.get(i).getStudents().add(studentList.get(random.nextInt(3)));
                studentList.get(i).getCourses().add(courseList.get(random.nextInt(3)));
            }

        courseService.save(math).block();
        courseService.save(physics).block();
        courseService.save(chemistry).block();

        ali.setName("Ali2");
        studentService.save(ali).block();
//        studentService.save(ali).subscribe();
//        studentService.save(hassan).subscribe();
//        studentService.save(hossein).subscribe();
    }
}
