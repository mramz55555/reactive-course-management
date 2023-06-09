package com.isoft.coursemanagement.services;

import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.models.StudentCourse;
import com.isoft.coursemanagement.repositories.CourseRepository;
import com.isoft.coursemanagement.repositories.StudentCourseRepository;
import com.isoft.coursemanagement.repositories.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "student with id: " + id + " not found")));
    }


    public Mono<Student> save(Student student) {
        return studentRepository.save(student)
                .flatMap(savedStudent ->
                        Flux.fromIterable(savedStudent.getCourses())
                                .flatMap(course ->
                                        courseRepository.findByName(course.getName())
                                                .switchIfEmpty(courseRepository.save(course))
                                                .flatMap(savedCourse ->
                                                        studentCourseRepository.save(new StudentCourse(savedCourse.getId(), savedStudent.getId()))
                                                                .then(Mono.empty())
                                                )
                                )
                                .then(Mono.just(savedStudent))
                );
    }


    public Mono<Boolean> enrollCourse(int studentId, int courseId) {
        return studentCourseRepository.findByCourseId(courseId).count()
                .flatMap(co -> courseRepository.findById(courseId)
                        .flatMap(c -> {
                                    if (c.getCapacity() == co)
                                        return Mono.error(new IllegalArgumentException("capacity of this course is full"));
                                    if (studentRepository.findById(studentId).equals(Mono.empty()) || courseRepository.findById(courseId).equals(Mono.empty()))
                                        return Mono.error(new IllegalArgumentException("invalid param"));
                                    return studentCourseRepository.save(new StudentCourse(courseId, studentId)).then();
                                }
                        ).then(Mono.just(true)));
    }


    public Mono<Void> deleteAll() {
        return studentCourseRepository.deleteAll()
                .then(studentRepository.deleteAll());
    }
}
