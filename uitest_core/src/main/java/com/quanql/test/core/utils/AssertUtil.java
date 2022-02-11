package com.quanql.test.core.utils;

import org.testng.Assert;

public class AssertUtil {

  public static void fail(String message) {
    Assert.fail(message);
  }

  public static void assertEquals(String actual, String expected, String message) {
    Assert.assertEquals(actual, expected, message);
  }

  public static void assertTrue(boolean condition, String message) {
    Assert.assertTrue(condition, message);
  }
}
