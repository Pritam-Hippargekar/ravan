//package com.ayush.ravan.model;
//
//import java.util.HashSet;
//import java.util.Set;
//import javax.persistence.*;
//
//import lombok.Getter;
//import lombok.Setter;
//@Getter
//@Setter
//@Entity
//@Table(name = "student")
//public class Student {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
////    @Column(name = "student_no", nullable = false, length = 20, unique = true)
////    private String studentNo;
//
//    @Column(name = "first_name", nullable = false, length = 50)
//    private String firstName;
//
//    @Column(name = "last_name", nullable = false, length = 50)
//    private String lastName;
//
//    @ManyToMany(fetch = FetchType.LAZY,cascade = { CascadeType.MERGE, CascadeType.PERSIST })
//    @JoinTable(name = "student_course",
//            joinColumns = { @JoinColumn(name = "student_id") },
//            inverseJoinColumns = { @JoinColumn(name = "course_id") })
//    //@JsonIgnoreProperties("students")
//    private Set<Course> courses;
//
//    public void addCourse(Course course) {
//        this.courses.add(course);
//        course.getStudents().add(this);
//    }
//    public void removeCourse(Course course) {
//        this.getCourses().remove(course);
//        course.getStudents().remove(this);
//    }
//    public void removeCourses() {
//        for (Course course : new HashSet<>(courses)) {
//            removeCourse(course);
//        }
//    }
//
//    public void removeTag(long tagId) {
//        Tag tag = this.tags.stream().filter(t -> t.getId() == tagId).findFirst().orElse(null);
//        if (tag != null) {
//            this.tags.remove(tag);
//            tag.getTutorials().remove(this);
//        }
//    }
//
//}
