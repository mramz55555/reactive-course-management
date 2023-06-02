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
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;

    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository, StudentCourseRepository studentCourseRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    public Flux<Student> getStudents(int id) {
        return studentRepository.findAllById(studentCourseRepository.findByCourseId(id).map(StudentCourse::getStudentId));
    }

    public Mono<Course> getCourse(int id) {
        return courseRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "course  with id: " + id + " not found")));
    }

    public Mono<Course> save(Course course) {
        Course course2 = courseRepository.save(course).block();
        //cascade saving
        course2.getStudents().forEach(s -> {
            if (studentRepository.findById(s.getId()).block() == null)
                studentRepository.save(s).block();
            studentCourseRepository.save(new StudentCourse(s.getId(), course.getId())).block();
            studentRepository.save(s).block();
        });
        return Mono.just(course2);
    }

    public Mono<Void> deleteAll() {
        courseRepository.findAll().flatMap(c -> studentCourseRepository.deleteByCourseId(c.getId())).blockLast();
//        studentRepository.findAll()
//                .doOnNext(s -> template.delete(query(where("student_id").is(s.getId())), StudentCourse.class).block());
        return courseRepository.deleteAll();
    }
}
