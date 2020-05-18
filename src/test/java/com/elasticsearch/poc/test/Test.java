package com.elasticsearch.poc.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
public class Test {
    public static void main(String args[]) {
     List<String> options = Arrays.asList("java", "javascript","mongo", "java","java");
     
     Function<List<String>, String> function =  input -> {
    	 return (String)Collections.max(input.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()))
    			 .entrySet(), (e1,e2) -> e1.getValue().intValue() - e2.getValue().intValue()).getKey();
     };
   System.out.println("Most repeated word is : " + function.apply(options));
  }
}
