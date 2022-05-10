package com.jamf.reporting.junit

import static org.junit.Assert.fail

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import com.jamf.reporting.TestRailTestMatcher

@ReportJUnitResults
class VerifyJUnitListener {

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
  @Disabled
  void skip() {
  }

  @Test
  @Disabled
  @TestRailTestMatcher([1234])
  void skip_customMatch() {
  }

  @ParameterizedTest
  @ValueSource(strings = ["param 1", "param 2"])
  void parameterised(String param) {
  }

  @ParameterizedTest
  @ValueSource(strings = ["param 1", "param 2"])
  @TestRailTestMatcher([1234])
  void parameterised_customMatch(String param) {
  }
}
