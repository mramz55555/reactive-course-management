package com.isoft.coursemanagement.services;

import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.repositories.CourseRepository;
import com.isoft.coursemanagement.repositories.StudentCourseRepository;
import com.isoft.coursemanagement.repositories.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class StudentServiceTest {
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentService studentService;
    private Student testStudent;
    private Student resultStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student("Test Student");
        Course course = new Course("Test Ahmad", 10);
        testStudent.getCourses().add(course);

        resultStudent = new Student();
        resultStudent.setId(studentService.save(testStudent).block().getId());
        resultStudent.setName("Test Student");
        resultStudent.getCourses().add(course);
    }

    @AfterEach
    void tearDown() {
        studentService.deleteAll().block();
        courseService.deleteAll().block();
    }

    @Test
    void save() {
        StepVerifier.create(Mono.just(testStudent))
                .then(() -> {
                    StepVerifier.create(studentRepository.count())
                            .expectNext(1L)
                            .verifyComplete();

                    StepVerifier.create(courseRepository.count())
                            .expectNext(1L)
                            .verifyComplete();

                    StepVerifier.create(studentCourseRepository.count())
                            .expectNext(1L)
                            .verifyComplete();

//                    //checking for saving duplicate relation
//                    StepVerifier
//                            .create(studentCourseRepository.findAll().collectList()
//                                    .flatMap(sc -> {
//                                        Student student = new Student();
//                                        student.setId(sc.get(0).getStudentId());
//
//                                        Course course = new Course();
//                                        course.setId(sc.get(0).getCourseId());
//
//                                        student.getCourses().add(course);
//                                        return studentService.save(student);
//                                    }).then(studentCourseRepository.count()))
//                            .expectNext(1L)
//                            .verifyComplete();
//
//                    //checking for duplicate student
//                    StepVerifier
//                            .create(studentRepository.findAll().flatMap(c1 -> {
//                                Student student = new Student();
//                                student.setId(c1.getId());
//                                return studentService.save(student);
//                            }).then(studentRepository.count()))
//                            .expectNext(1L)
//                            .verifyComplete();
//
//                    //checking for duplicate course
//
//                    StepVerifier
//                            .create(courseRepository.findAll().next().flatMap(c -> {
//                                Course course = new Course();
//                                course.setId(c.getId());
//                                Student student = new Student("");
//
//                                student.getCourses().add(course);
//                                return studentService.save(student);
//                            }).then(courseRepository.count()))
//                            .expectNext(1L)
//                            .verifyComplete();


                })
                .expectNext(resultStudent)
                .verifyComplete();
    }

    @Test
    void enrollCourse() {

        Student student = new Student("");
        Course course = new Course();

        StepVerifier
                .create(studentService.save(student)
                        .flatMap(s -> {
                            student.setId(s.getId());
                            return courseService.save(course)
                                    .flatMap(c -> {
                                        course.setId(c.getId());
                                        return studentService.enrollCourse(course.getId(), student.getId());
                                    });
                        }).then(studentCourseRepository.count()))
                .expectNext(2L)
                .verifyComplete();


        //invalid student
        StepVerifier
                .create(studentRepository.findAll()
                        .then(studentService.enrollCourse(course.getId(), student.getId() * 2)))
                .expectError(IllegalArgumentException.class)
                .verify();


        //invalid course
        StepVerifier
                .create(courseRepository.findAll().
                        then(studentService.enrollCourse(course.getId() * 2, student.getId())))
                .expectError(IllegalArgumentException.class)
                .verify();

    }

    @Test
    void deleteEnrollment() {

        Student student = new Student("");
        Course course = new Course();

        StepVerifier
                .create(studentService.save(student).flatMap(s -> {
                    student.setId(s.getId());
                    return courseService.save(course)
                            .flatMap(c -> {
                                course.setId(c.getId());
                                return studentService.enrollCourse(course.getId(), student.getId());
                            });
                }))
                .then(() -> {
                    StepVerifier
                            .create(studentService.deleteEnrollment(course.getId(), student.getId())
                                    .then(studentCourseRepository.count()))
                            .expectNext(1L)
                            .verifyComplete();

                    StepVerifier.create(studentService.deleteEnrollment(course.getId(), student.getId()))
                            .expectNext(false)
                            .verifyComplete();
                })
                .expectNext(true)
                .verifyComplete();
    }
}