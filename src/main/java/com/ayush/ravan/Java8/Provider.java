package com.ayush.ravan.Java8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Provider {
    private Integer id;
    private String name;
    private Integer age;

    private List<String> hobbies;
    private String departmentName;
    private Double salary;
//    private BigDecimal price;

    public void addDepartment(String book) {
        if (this.hobbies == null) {
            this.hobbies = new ArrayList<>();
        }
        this.hobbies.add(book);
    }
}
