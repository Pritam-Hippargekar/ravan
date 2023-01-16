package com.ayush.ravan.recur;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeTestDemo {

    public static void main(String[] args) {
        List<Employee> list = Arrays.asList(
                new Employee(1, Optional.empty()),
                new Employee(2, Optional.of(6)),
                new Employee(3, Optional.of(6)),
                new Employee(4, Optional.of(1)),
                new Employee(5, Optional.of(1)),
                new Employee(6, Optional.of(1)),
                new Employee(7, Optional.ofNullable(10))
        );

        Map<Optional<Integer>, List<Employee>> demoList = list.stream()
//                .filter(data->{
//                    list.forEach(item->item.empId == data.getEmpId());
//                })
                .collect(Collectors.groupingBy(Employee::getManagerId));
        demoList.forEach((key,value)-> {
            if(key.isPresent()){
                System.out.println("Manager : "+key.get());
                value.stream().forEach(data->System.out.println(data.empId));
            }
        } );
    }
}
