package com.isoft.coursemanagement.repositories;

import com.isoft.coursemanagement.models.Course;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course, Integer> {
}
