package com.ayush.ravan.controller;

import com.ayush.ravan.beans.Product;
import com.ayush.ravan.beans.PrototypeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestPrototypeInSingletonController {
    @Autowired
    private PrototypeBean prototypeBean;

    @RequestMapping(value = "/addProduct/{productName}")
    public String testAdd(@PathVariable(value="messageId") Integer messageId,@PathVariable(value = "message") String message) {
        Product product = new Product();
        product.setMessage(message);
        product.setMessageId(messageId);
        this.prototypeBean.addProduct(product);
        System.out.println("ShoppingCart is "+this.prototypeBean);
        return "test";
    }
}
