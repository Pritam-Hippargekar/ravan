package com.ayush.ravan.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
//    Singleton is adapted to stateless beans
@Component
@Scope( ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SingletonBean {

    @Autowired
    private PrototypeBean prototypeBean;

    public SingletonBean() {
        System.out.println("Creating Singleton instance ............");
    }

    public PrototypeBean getPrototypeBean() {
        return prototypeBean;
    }

}


//    SingletonBean singletonBean1 = context.getBean(SingletonBean.class);
//    SingletonBean singletonBean2 = context.getBean(SingletonBean.class);
//if(singletonBean1.getPrototypeBean() == singletonBean2.getPrototypeBean()) {
//        System.out.println(“Only one instance created for PrototypeBean”);
//        }
