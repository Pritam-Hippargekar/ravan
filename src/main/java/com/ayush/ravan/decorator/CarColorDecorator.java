package com.ayush.ravan.decorator;

public class CarColorDecorator extends CarDecorator{

    public CarColorDecorator(Car carDecorator) {
        super(carDecorator);
    }

    @Override
    public void paint() {
        decoratedCar.paint();
        setTheme(decoratedCar);
    }

    private void setTheme(Car car){
        System.out.println("Adding Extra color to car.");
    }
}
