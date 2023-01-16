package com.ayush.ravan.decorator;

public class ElectricCar implements Car{
    @Override
    public void paint() {
      System.out.println("ElectricCar");
    }
}
