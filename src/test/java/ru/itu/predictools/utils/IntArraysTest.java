package ru.itu.predictools.utils;

import junit.framework.TestCase;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class IntArraysTest extends TestCase {
  
  public void testMin() {
    int[] a = {8, 3, 4, 7, 9, 1, 2, 6, 5, 10};
    assertEquals(1, IntArrays.min(a, (n1, n2) -> (n1 > n2)));
    a = new int[]{8};
    assertEquals(8, IntArrays.min(a, (n1, n2) -> (n1 > n2)));
    a = new int[]{8,10};
    assertEquals(8, IntArrays.min(a, (n1, n2) -> (n1 > n2)));
  }
  
  public void testSort() {
    int[] a = {0, 1, 2, 13, 4, 5, 6, 17, 8, 9};
    int[] expectation = {0, 1, 2, 4, 5, 6, 8, 9, 13, 17};
    assertThat(expectation, not(equalTo(a)));
    IntArrays.sort(a, (n1, n2) -> (n1 > n2));
    assertArrayEquals(expectation, a);
    a = new int[]{};
    expectation = new int[]{};
    IntArrays.sort(a, (n1, n2) -> (n1 > n2));
    assertArrayEquals(expectation, a);
    a = new int[]{8};
    expectation = new int[]{8};
    IntArrays.sort(a, (n1, n2) -> (n1 > n2));
    assertArrayEquals(expectation, a);
  }
}