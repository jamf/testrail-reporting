package com.jamf.reporting.spock

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import com.jamf.reporting.TestRailTestMatcher

@ReportSpockResults
class VerifySpockListener extends Specification {

  void "success"() {
    expect:
    1 == 1
  }

  @TestRailTestMatcher(1234)
  void "success - custom matching"() {
    expect:
    1 == 1
  }

  @TestRailTestMatcher([1234, 4321])
  void "success - custom matching - multiple values"() {
    expect:
    1 == 1
  }

  void "fail"() {
    expect:
    0 == 1
  }

  @TestRailTestMatcher(1234)
  void "fail - custom matching"() {
    expect:
    0 == 1
  }

  @Ignore
  void "skip"() {
    expect:
    1 == 1
  }

  @Ignore
  @TestRailTestMatcher(1234)
  void "skip - custom matching"() {
    expect:
    1 == 1
  }

  @Unroll
  void "parameterised (#left ?? #right)"() {
    expect:
    left == right

    where:
    left | right
    1    | 1
    2    | 2
  }

  @Unroll
  @TestRailTestMatcher(1234)
  void "parameterised (#left ?? #right) - custom matching"() {
    expect:
    left == right

    where:
    left | right
    1    | 1
    2    | 2
  }
}
