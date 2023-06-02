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
        Student student2 = studentRepository.save(student).block();

        //cascade saving.
        student.getCourses().forEach(c -> {
            if (courseRepository.findById(c.getId()).block() == null) {
                studentCourseRepository.save(new StudentCourse(student.getId(), c.getId())).block();
                courseRepository.save(c).block();
            }
        });
        return Mono.just(student2);
    }

    public Mono<Void> deleteAll() {
        studentRepository.findAll().flatMap(c -> studentCourseRepository.deleteByStudentId(c.getId())).blockLast();
        return studentRepository.deleteAll();
    }
}
