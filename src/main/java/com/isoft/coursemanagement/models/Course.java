package com.isoft.coursemanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "course")
public class Course {
    @Id
    private int id;
    private String name;

    @JsonIgnore
    @Transient
    private Set<Student> students = new HashSet<>();

    public Course(String name) {
        this.name = name;
    }
}
