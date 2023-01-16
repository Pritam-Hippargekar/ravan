package com.ayush.ravan.Java8;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestJava8 {

    public static void main(String[] args) {
//      Map<String,List<Provider>> departWise=
//              getArrayListData().stream().collect(Collectors.groupingBy(Provider::getDepartment,Collectors.counting() ))
//              .entrySet().forEach((key)->System.out.println(key));


//        new TestJava8().ravanMethod("null");
//          Parent child = new Child();
//        child.n();
//        child.m();


      // each dept how many employees/ find duplicate
//      Map<String, Long> lst=  getArrayListData().stream().collect(Collectors.groupingBy(Provider::getDepartmentName,Collectors.counting()));
//      lst.forEach((key,value)->System.out.println(key+" VS "+value));

       // depart wise max salary
//        Map<String, Optional<Provider>> lst = getArrayListData().stream()
//                .collect(Collectors.groupingBy(Provider::getDepartmentName,Collectors.maxBy(Comparator.comparing(Provider::getSalary))));
//        System.out.println(lst);

        Map<String, List<Provider>> lst = getArrayListData().stream()
                .collect(Collectors.groupingBy(Provider::getDepartmentName,TreeMap::new,Collectors.toList()));
        lst.forEach((key,value)->System.out.println(key+" VS "+value));

    }


    public static Provider getNewProvider(Provider current){
        throw new RuntimeException("RuntimeException.....");
    }

    public static List<Provider> getArrayListData(){
        return Arrays.asList(
                new Provider(10,"Ayushamn",19,Arrays.asList("Java","Spring","Jpa"),"AA",150d),
                new Provider(11,"Ravan",23,Arrays.asList(),"CCCC",150d),
                new Provider(22,"Pooja",10,Arrays.asList(),"BBB",200d),
                new Provider(9,"Ashiwini",34,Arrays.asList(),"CCCC",854d),
                new Provider(5,"Pallavi",55,Arrays.asList("Python","PyChamp","Mysql"),"AA",3654d),
                new Provider(20,"Mona",33,Arrays.asList(),"CCCC",234d),
                new Provider(15,"Nagin",50,Arrays.asList(),"BBB",222d),
                new Provider(12,"Pritam",44,Arrays.asList(),"CCCC",7777d),
                new Provider(1,"Balu",20,Arrays.asList("Lavavel","Apache2"),"AA",999d)
                );
    }

//    public void ravanMethod(Number str){
//        System.out.println("Inside Number");
//    }

    public void ravanMethod(Integer str){
        System.out.println("Inside Integer");
    }

    public void ravanMethod(String str){
        System.out.println("Inside String");
    }
}
