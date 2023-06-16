package com.isoft.coursemanagement.services;

import com.isoft.coursemanagement.CourseManagementApplication;
import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.models.StudentCourse;
import com.isoft.coursemanagement.repositories.CourseRepository;
import com.isoft.coursemanagement.repositories.StudentCourseRepository;
import com.isoft.coursemanagement.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository, StudentCourseRepository studentCourseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    public Flux<Course> getCourses(int id) {
        return courseRepository.findAllById(studentCourseRepository.findByStudentId(id).map(StudentCourse::getCourseId));
    }


    public Mono<Student> getStudent(int id) {
        return studentRepository
                .findById(id)
                .switchIfEmpty(CourseManagementApplication.monoError(id));
    }


    public Mono<Student> save(Student student) {
        return studentRepository.save(student)
                .flatMap(savedStudent ->
                        Flux.fromIterable(savedStudent.getCourses())
                                .flatMap(course -> courseRepository.save(course)
                                        .flatMap(savedCourse -> {
                                                    course.setId(savedCourse.getId());
                                                    return studentCourseRepository.save(new StudentCourse(savedCourse.getId(), savedStudent.getId()));
                                                }
                                        )
                                ).then(Mono.just(savedStudent))
                );
    }

    public Mono<Boolean> enrollCourse(int courseId, int studentId) {
        return studentCourseRepository.findByCourseId(courseId)
                .collectList()
                .flatMap(l -> courseRepository.findById(courseId)
                        .switchIfEmpty(CourseManagementApplication.monoError(courseId))
                        .flatMap(c -> l.size() == c.getCapacity() ?
                                Mono.error(new IllegalArgumentException("capacity of this course is full")) :
                                studentRepository.findById(studentId).switchIfEmpty(CourseManagementApplication.monoError(studentId))
                                        .flatMap(s -> studentCourseRepository.save(new StudentCourse(courseId, studentId)))
                                        .then(Mono.just(true))));
    }


    public Mono<Boolean> deleteEnrollment(int studentId, int courseId) {
        return studentCourseRepository.findByStudentId(studentId)
                .filter(sc -> sc.getStudentId() == studentId && sc.getCourseId() == courseId)
                .flatMap(sc -> studentCourseRepository.deleteByCourseIdAndStudentId(courseId, studentId)
                        .thenReturn(true))
                .single(false);
    }


    public Mono<Void> deleteAll() {
        return studentCourseRepository.deleteAll()
                .then(studentRepository.deleteAll());
    }
}
