package com.jamf.reporting.testng

import static org.testng.Assert.fail

import org.testng.SkipException
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import com.jamf.reporting.TestRailTestMatcher

class VerifyTestNGListener_setup {

  @BeforeClass
  void setup() {
    throw new RuntimeException("Setup failed!")
  }

  @Test
  void success() {
  }

  @Test
  @TestRailTestMatcher([1234])
  void success_customMatch() {
  }

  @Test
  @TestRailTestMatcher([1234, 4321])
  void success_customMatch_multipleValues() {
  }

  @Test
  void failTest() {
    fail("Should report failed test.")
  }

  @Test
  @TestRailTestMatcher([1234])
  void failTest_customMatch() {
    fail("Should report failed test.")
  }

  @Test
  void skip() {
    throw new SkipException("Skip")
  }

  @Test
  @TestRailTestMatcher([1234])
  void skip_customMatch() {
    throw new SkipException("Skip")
  }

  @Test(dataProvider = "data")
  void parameterised(String param) {
  }

  @Test(dataProvider = "data")
  @TestRailTestMatcher([1234])
  void parameterised_customMatch(String param) {
  }

  @DataProvider(name = "data")
  Object[][] getData() {
    return [["param1"], ["param2"]]
  }
}
