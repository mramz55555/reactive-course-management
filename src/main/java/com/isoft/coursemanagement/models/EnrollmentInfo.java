package com.isoft.coursemanagement.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class EnrollmentInfo {
    private Course course;
    private Set<Student> students = new HashSet<>();
    private int remainingCapacity;


    @Override
    public int hashCode() {
        int result = course != null ? course.hashCode() : 0;
        result = 31 * result + (students != null ? students.hashCode() : 0);
        result = 31 * result + remainingCapacity;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnrollmentInfo that = (EnrollmentInfo) o;

        return remainingCapacity == that.remainingCapacity && course.equals(that.course) && students.equals(that.students);
    }
}
