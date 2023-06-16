package com.isoft.coursemanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@Setter
@NoArgsConstructor
@Table(name = "course")
public class Course {
    @Id
    private int id;
    private String name;
    private int capacity = 10;

    @JsonIgnore
    @Transient
    private Set<Student> students = new HashSet<>();

    public Course(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + capacity;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;
        return course.id == id && name.equals(course.name);
    }

}
