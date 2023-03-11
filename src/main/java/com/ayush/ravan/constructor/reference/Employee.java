package com.ayush.ravan.constructor.reference;

public class Employee {
    String eName;
    int eAge;
    String gender;

    public Employee() {
    }

    public Employee(String eName, int eAge, String gender) {
        this.eName = eName;
        this.eAge = eAge;
        this.gender = gender;
    }
}
