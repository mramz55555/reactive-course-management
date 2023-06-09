package com.isoft.coursemanagement.controllers;

import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.services.StudentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public Mono<Student> getStudent(@PathVariable int id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/{id}/courses")
    public Flux<Course> getCourses(@PathVariable int id) {
        return studentService.getCourses(id);
    }

    @PostMapping
    public Mono<Student> saveCourse(@RequestBody Student student) {
        return studentService.save(student);
    }

    @PostMapping("/{id}/enrollCourse")
    public Mono<Boolean> enrollCourse(@PathVariable(name = "id") int studentId, @RequestParam int courseId) {
        return studentService.enrollCourse(studentId, courseId);
    }

}
