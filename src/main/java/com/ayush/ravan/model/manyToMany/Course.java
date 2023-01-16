//package com.ayush.ravan.model;
//
//import java.util.HashSet;
//import java.util.Set;
//import javax.persistence.*;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Getter;
//import lombok.Setter;
//@Getter
//@Setter
//@Entity
//@Table(name = "course")
//public class Course {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(name = "course_name", nullable = false, length = 50, unique = true)
//    private String courseName;
//
//    @ManyToMany(mappedBy = "courses",cascade = { CascadeType.MERGE, CascadeType.PERSIST },fetch = FetchType.LAZY)
//    @JsonIgnore
//    //@JsonIgnoreProperties("courses")
//    private Set<Student> students;
//
//    public void removeStudent(Student student) {
//        this.getStudents().remove(student);
//        student.getCourses().remove(this);
//    }
//
//    public void removeStudents() {
//        for (Student student : new HashSet<>(students)) {
//            removeStudent(student);
//        }
//    }
//}
