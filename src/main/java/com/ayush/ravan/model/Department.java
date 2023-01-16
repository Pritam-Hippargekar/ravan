//package com.ayush.ravan.model;
//
//import java.util.Set;
//import javax.persistence.*;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.AllArgsConstructor;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//@Entity
//@Table(name = "Department")
//@Getter
//@Setter
//@ToString
//@AllArgsConstructor
//@NoArgsConstructor
//@EqualsAndHashCode
//public class Department {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Integer id;
//
//    @Column(name = "deptName")
//    private String deptName;
//
//    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER, mappedBy = "department", orphanRemoval = true)
//    @JsonIgnore
////    @JsonIgnoreProperties("company") // for employee field company
//    private Set<Employee> employees;
//
//    public void addEmployee(Employee book){
//        employees.add(book);
//        book.setDepartment(this);
//    }
//    public void removeEmployee(Employee book){
//        employees.remove(book);
//        book.setDepartment(null);
//    }
//}
