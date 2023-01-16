package com.ayush.ravan.recur;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class Employee {

    int empId;
    Optional<Integer> managerId;
}
