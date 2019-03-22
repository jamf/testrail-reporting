package com.jamf.reporting;

import static org.junit.Assert.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.jamf.reporting.junit.ReportJUnitResults;

@ReportJUnitResults
public class VerifyJUnitListener {

  @Test
  public void success() {
  }

  @Test
  public void failTest() {
    fail();
  }

  @Test
  @Disabled
  public void skip() {
  }

  @Test
  @com.jamf.reporting.junit.Disabled
  public void custom_skip() {
  }

  @ParameterizedTest
  @ValueSource(strings = {"param 1", "param 2"})
  public void parameterised(String param) {
    System.out.println(param);
  }

}
