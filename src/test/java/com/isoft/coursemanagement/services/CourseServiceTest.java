package com.isoft.coursemanagement.services;

import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.EnrollmentInfo;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.repositories.CourseRepository;
import com.isoft.coursemanagement.repositories.StudentCourseRepository;
import com.isoft.coursemanagement.repositories.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class CourseServiceTest {
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
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course("Test Course", 10);
        testCourse.getStudents().add(new Student("Test Ahmad"));
        testCourse.getStudents().add(new Student("Test Abbas"));
        testCourse.getStudents().add(new Student("Test ALi"));

        courseService.save(testCourse).block();
    }

    @AfterEach
    void tearDown() {
        courseService.deleteAll().block();
        studentService.deleteAll().block();
    }

    @Test
    @Disabled
    void save() {
        StepVerifier.create(courseService.save(testCourse))
                .then(() -> {
                    StepVerifier.create(courseRepository.count())
                            .expectNext(1L)
                            .verifyComplete();

                    StepVerifier.create(studentRepository.count())
                            .expectNext(3L)
                            .verifyComplete();

                    StepVerifier.create(studentCourseRepository.count())
                            .expectNext(3L)
                            .verifyComplete();

                    //checking for saving duplicate relation
                    StepVerifier
                            .create(studentCourseRepository.findAll().collectList()
                                    .flatMap(sc -> {
                                        Student student = new Student();
                                        student.setId(sc.get(0).getStudentId());

                                        Course course = new Course();
                                        course.setId(sc.get(0).getCourseId());

                                        course.getStudents().add(student);
                                        return courseService.save(course);
                                    }).then(studentCourseRepository.count()))
                            .expectNext(3L)
                            .verifyComplete();

                    //checking for duplicate course
                    StepVerifier
                            .create(courseRepository.findAll().flatMap(c1 -> {
                                Course course = new Course();
                                course.setId(c1.getId());
                                return courseService.save(course);
                            }).then(courseRepository.count()))
                            .expectNext(1L)
                            .verifyComplete();

                    //checking for duplicate student
                    StepVerifier
                            .create(studentRepository.findAll().next().flatMap(s -> {
                                Course course = new Course();
                                Student student = new Student();
                                student.setId(s.getId());
                                course.getStudents().add(student);
                                return courseService.save(course);
                            }).then(studentRepository.count()))
                            .expectNext(3L)
                            .verifyComplete();

                    //checking for invalid capacity
                    Course course = new Course();
                    course.setCapacity(0);
                    StepVerifier
                            .create(courseService.save(course))
                            .expectError(IllegalArgumentException.class)
                            .verify();

                })
                .expectNext(testCourse)
                .verifyComplete();
    }

    @Test
    void getEnrollmentInfo() {
        EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
        enrollmentInfo.setCourse(testCourse);
        enrollmentInfo.getStudents().addAll(testCourse.getStudents());
        enrollmentInfo.setRemainingCapacity(testCourse.getCapacity() - testCourse.getStudents().size());

        StepVerifier
                .create(courseService.getEnrollmentInfo(testCourse.getId()))
                .expectNext(enrollmentInfo)
                .verifyComplete();

        StepVerifier
                .create(courseService.getEnrollmentInfo(testCourse.getId() + 1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}