package com.ayush.ravan.decorator;

public class DecoratorTest {
    public static void main(String[] args) {
//        Car defaultHybridCar = new HybridCar();
        Car redHybridCar = new CarColorDecorator(new HybridCar());
        Car blueElectricCar = new CarColorDecorator(new ElectricCar());
//        defaultHybridCar.paint();
        redHybridCar.paint();
        blueElectricCar.paint();
    }
}
