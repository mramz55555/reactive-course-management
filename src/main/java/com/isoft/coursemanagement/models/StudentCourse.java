package com.isoft.coursemanagement.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("student_course")
public class StudentCourse {
    @Id
    private int id;
    @Column("course_id")
    private int courseId;
    @Column("student_id")
    private int studentId;

    public StudentCourse(int courseId, int studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
