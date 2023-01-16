package com.ayush.ravan.Java8;

import java.io.Serializable;

public class Singleton implements Cloneable, Serializable {
    private static Singleton instance = null;
    private Singleton(){
        if (instance != null) {
            throw new RuntimeException("You have broken Singleton class!");
        }
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

//    @Override
//    public Singleton clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException();
//    }

    @Override
    public Singleton clone() {
        return instance;
    }

    public Object readResolve() {
        return instance;
    }

}
