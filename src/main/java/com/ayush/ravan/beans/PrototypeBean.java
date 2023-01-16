package com.ayush.ravan.beans;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//    The prototype scope is better for stateful beans to avoid multithreading issues.
@Component
@Scope( ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeBean {

    List<Product> products = new ArrayList<>();

    public PrototypeBean() {
        System.out.println("Creating Prototype instance ............");
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product){
        this.products.add(product);
    }
}
