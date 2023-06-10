package com.isoft.coursemanagement.controllers;

import com.isoft.coursemanagement.models.Course;
import com.isoft.coursemanagement.models.EnrollmentInfo;
import com.isoft.coursemanagement.models.Student;
import com.isoft.coursemanagement.services.CourseService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    public Mono<Course> getCourse(@PathVariable int id) {
        return courseService.getCourse(id);
    }

    @GetMapping("/{id}/students")
    public Flux<Student> getStudent(@PathVariable int id) {
        return courseService.getStudents(id);
    }

    @PostMapping
    public Mono<Course> saveCourse(@RequestBody Course course) {
        return courseService.save(course);
    }

    @GetMapping("{id}/getEnrollmentInfo")
    public Mono<EnrollmentInfo> getEnrollmentInfo(@PathVariable(name = "id") int courseId) {
        return courseService.getEnrollmentInfo(courseId);
    }
}
