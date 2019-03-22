package com.jamf.reporting;

import static org.testng.Assert.fail;

import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jamf.reporting.testng.TestNGTestRunListener;
import com.jamf.reporting.testng.TestNGTestSuiteListener;

@Listeners(value = {TestNGTestRunListener.class, TestNGTestSuiteListener.class})
public class VerifyTestNGListener {

  @Test
  public void success() {
  }

  @Test
  public void failTest() {
    fail();
  }

  @Test
  public void skip() {
    throw new SkipException("Skip");
  }

  @Test(dataProvider = "data")
  public void parameterised(String param) {
    System.out.println(param);
  }

  @DataProvider(name = "data")
  public Object[][] getData() {
    return new Object[][]{
        new Object[]{"param1"},
        new Object[]{"param2"}
    };
  }
}
