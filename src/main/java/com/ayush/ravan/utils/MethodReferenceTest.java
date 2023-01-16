package com.ayush.ravan.utils;
import java.math.BigDecimal;

@FunctionalInterface
 interface GenerateEmployee {
    Employee getPaymentAmount(String name, Integer quantity,BigDecimal price);
}

 class Employee {
     private String name;
     private Integer quantity;
     private BigDecimal price;

     public Employee() {
         this.name="Ayushamn";
         this.quantity=10;
         this.price=BigDecimal.valueOf(10.25);
     }

     public Employee(String name, Integer quantity,BigDecimal price){
         this.name=name;
         this.quantity=quantity;
         this.price=price;
     }

     public Integer getQuantity() {
         return quantity;
     }

     public void setQuantity(Integer quantity) {
         this.quantity = quantity;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public BigDecimal checkAge(int inputAge){
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}

//public class MethodReferenceTest {
//    public static void main(String[] args) {
//        GenerateEmployee iFunctionalInterface = Employee::new;
//        Employee payAmount = iFunctionalInterface.getPaymentAmount("ayushman ravan",5,BigDecimal.valueOf(05.25));
//        System.out.println(payAmount.checkAge(11));
//    }
//}
