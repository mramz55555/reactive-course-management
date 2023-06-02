package com.isoft.coursemanagement.repositories;

import com.isoft.coursemanagement.models.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student, Integer> {
}
