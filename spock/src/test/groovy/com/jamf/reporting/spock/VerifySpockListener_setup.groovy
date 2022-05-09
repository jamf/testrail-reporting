package com.jamf.reporting.spock

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import com.jamf.reporting.TestRailTestMatcher

@ReportSpockResults
class VerifySpockListener_setup extends Specification {

  def setup() {
    throw new RuntimeException("Setup failed.")
  }

  void "success"() {
    expect:
    1 == 1
  }

  @TestRailTestMatcher(1234)
  void "success - custom matching"() {
    expect:
    1 == 1
  }

  void "fail"() {
    expect:
    0 == 1
  }

  @Ignore
  void "skip"() {
    expect:
    1 == 1
  }

  @Unroll
  @Ignore("This test is ignored until we figure out how to report it properly")
  void "parameterised (#left ?? #right)"() {
    expect:
    left == right

    where:
    left | right
    1    | 1
    1    | 2
  }
}
