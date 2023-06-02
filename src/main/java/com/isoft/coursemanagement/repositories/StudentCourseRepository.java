package com.isoft.coursemanagement.repositories;

import com.isoft.coursemanagement.models.StudentCourse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StudentCourseRepository extends ReactiveCrudRepository<StudentCourse, Integer> {
    Flux<StudentCourse> findByCourseId(int id);

    Flux<StudentCourse> findByStudentId(int id);

    Flux<Void> deleteByStudentId(int id);

    Flux<Void> deleteByCourseId(int id);
}
