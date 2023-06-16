package com.isoft.coursemanagement.services;

import com.isoft.coursemanagement.CourseManagementApplication;
import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.EnrollmentInfo;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.models.StudentCourse;
import com.isoft.coursemanagement.repositories.CourseRepository;
import com.isoft.coursemanagement.repositories.StudentCourseRepository;
import com.isoft.coursemanagement.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;

    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository,
                         StudentCourseRepository studentCourseRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
    }


    public Mono<Course> findById(int id) {
        return courseRepository.findById(id)
                .switchIfEmpty(CourseManagementApplication.monoError(id));
    }

    public Flux<Student> findAllStudentsById(int id) {
        return studentRepository.findAllById(studentCourseRepository.findByCourseId(id).map(StudentCourse::getStudentId));
    }


    public Mono<Course> save(Course course) {
        return courseRepository.save(course)
                .flatMap(savedCourse ->
                        Flux.fromIterable(savedCourse.getStudents())
                                .flatMap(student -> studentRepository.save(student)
                                        .flatMap(savedStudent -> {
                                                    student.setId(savedStudent.getId());
                                                    return studentCourseRepository.save(new StudentCourse(savedCourse.getId(), savedStudent.getId()));
                                                }
                                        )
                                ).then(Mono.just(savedCourse))
                );
    }

    public Mono<EnrollmentInfo> getEnrollmentInfo(int courseId) {
        EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
        return studentCourseRepository.findByCourseId(courseId)
                .switchIfEmpty(CourseManagementApplication.monoError(courseId))
                .collectList()
                .flatMap(l -> findById(courseId)
                        .doOnNext(c -> {
                            enrollmentInfo.setCourse(c);
                            enrollmentInfo.setRemainingCapacity(c.getCapacity() - l.size());

                        }).and(Flux.fromIterable(l)
                                .flatMap(sc -> studentRepository.findById(sc.getStudentId())
                                        .flatMap(s -> Mono.just(enrollmentInfo.getStudents().add(s))))))
                .thenReturn(enrollmentInfo);
    }


    public Mono<Void> deleteAll() {
        //because in this table each student certainly has a relation with a course,
        // so if we don't have any course, we don't have any relation
        return studentCourseRepository.deleteAll()
                .then(courseRepository.deleteAll());
    }

}
