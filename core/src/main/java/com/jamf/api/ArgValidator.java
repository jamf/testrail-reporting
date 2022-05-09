package com.jamf.api;

public class ArgValidator {

  public static void isNotNull(Object arg) {
    if (arg == null) {
      throw new IllegalArgumentException(String.format("Argument cannot be null"));
    }
  }

  public static void greaterThan(int arg, int digit) {
    if (!(arg > digit)) {
      throw new IllegalArgumentException(String.format("Argument has to be greater than %s", digit));
    }
  }
}
