package ru.itu;

import org.junit.Test;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class miscTests {
  @Test
  public void streamReduceTest() {
    int reducedParallel = Arrays.asList(new Integer[]{1, 2, 3}).parallelStream()
                              .reduce(10, (a, b) -> {
                                    Integer c = a + b;
                                    System.out.println("accumulator was called a + b = " + a + " + " + b + " = " + c);
                                    return c;
                                  },
                                  (a, b) -> {
                                    Integer c = a + b;
                                    System.out.println("combiner was called a + b = " + a + " + " + b + " = " + c);
                                    return c;
                                  });
  }
  
  @Test
  public void streamsCollectTest(){
    String[] arr = {"a", "b", "c", "d"};
    Set<String> characters = new HashSet<>(Arrays.asList(arr));
    String result = characters.stream().collect(Collectors.joining("-!-","{[--","--]}"));
    System.out.println(result);
  }
}
