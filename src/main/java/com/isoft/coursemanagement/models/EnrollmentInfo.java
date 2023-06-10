package com.isoft.coursemanagement.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EnrollmentInfo {
    private Course course;
    private List<Student> students = new ArrayList<>();
    private int remainingCapacity;
}
