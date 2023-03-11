package com.ayush.ravan.constructor.reference;

public class ConstructorRefTest {

    public static void main(String[] args) {
        ConstructorReference constructorReference = Employee::new;
        Employee object = constructorReference.AbstractMethod("ayushman",10,"male");
    }
}
