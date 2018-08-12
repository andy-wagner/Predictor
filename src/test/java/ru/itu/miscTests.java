package ru.itu;

import org.junit.Test;

import java.io.*;
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
  public void streamsCollectTest() {
    String[] arr = {"a", "b", "c", "d"};
    Set<String> characters = new HashSet<>(Arrays.asList(arr));
    String result = characters.stream().collect(Collectors.joining("-!-", "{[--", "--]}"));
    System.out.println(result);
  }
  
  @Test
  public void convertDictionary() throws IOException {
    String line;
    String[] lineFields;
    int n = 0;
    try (
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "dictionaries" + File.separator + "en.dic"));
        BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "dictionaries" + File.separator + "en-main-v1-utf8.dic", true))) {
      while ((line = reader.readLine()) != null) {
        lineFields = line.split("\\s");
        line = Integer.toString(++n) + "," + lineFields[1] + "," + lineFields[0] +"\r\n";
        writer.append(line);
      }
    } catch (IOException e) {
      throw new IOException(e);
    }
  }
}
