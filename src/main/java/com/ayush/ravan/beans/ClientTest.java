package com.ayush.ravan.beans;



//package config;
//        -- imports --
//@Configuration
//@ComponentScan(basePackages = {"components"})
//public class Config {}






public class ClientTest {


//    public static void main(String[] args) {
//
//        ApplicationContext applicationContext = null;
//        var context = new AnnotationConfigApplicationContext(Config.class);
//        try {
//
//            //Creating Instance of ApplicationContext Spring Container
//            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
//
//            //Asking Spring Container to return Spring bean with id "message"
//            Object object = applicationContext.getBean("message");
//            //Covert Spring bean into your business Object
//            Message message = (Message) object;
//            message.setMessageId(101);
//            message.setMessage("Hello World");
//
//            //Print Spring bean state
//            System.out.println(message.getMessageId() + "\t" + message.getMessage());
//
//            Object object2 = applicationContext.getBean("message");
//            //Covert Spring bean into your business Object
//            Message message2 = (Message) object2;
//            System.out.println(message2.getMessageId() + "\t" + message2.getMessage());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (applicationContext != null)
//                ((AbstractApplicationContext) applicationContext).close();
//        }
//    }

//    public static void main(String[] args) {
//        try {
//            // retreive two different contexts
//            ApplicationContext firstContext = new FileSystemXmlApplicationContext("/home/bartosz/webapp/src/main/resources/META-INF/applicationContext.xml");
//            ApplicationContext secondContext = new FileSystemXmlApplicationContext("/home/bartosz/webapp/src/main/resources/META-INF/applicationContext.xml");
//
//            // compare the objects from different contexts
//            ShoppingCart firstShoppingCart = (ShoppingCart) firstContext.getBean("shoppingCart");
//            ShoppingCart secondShoppingCart = (ShoppingCart) secondContext.getBean("shoppingCart");
//            System.out.println("1. Are they the same ? " + (firstShoppingCart == secondShoppingCart));
//
//            // compare the objects from the same context
//            ShoppingCart firstShoppingCartBis = (ShoppingCart) firstContext.getBean("shoppingCart");
//            System.out.println("2. Are they the same ? "+ (firstShoppingCart == firstShoppingCartBis));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



}