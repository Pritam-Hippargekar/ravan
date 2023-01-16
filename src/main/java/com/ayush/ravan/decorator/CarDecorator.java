package com.ayush.ravan.decorator;

public abstract  class CarDecorator implements Car{
    protected Car decoratedCar;

    public CarDecorator(Car carDecorator){
        this.decoratedCar = carDecorator;
    }

    public void paint(){
        decoratedCar.paint();
    }
}
