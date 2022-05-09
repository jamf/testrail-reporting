package com.jamf.reporting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestRailTestMatcher {

  /**
   * The {@code fieldName} value to be used to match against test case.
   *
   * <p>"C" prefix from case number must be dropped, use 1234 instead of C1234
   */
  int[] value();

  /**
   * The {@code fieldName} name to be used to match against test case.
   *
   * <p>Defaults to {@code id}
   */
  String fieldName() default "id";
}
