package com.jamf.now.qa.reporting

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import com.jamf.reporting.spock.ReportSpockResults

@ReportSpockResults
class VerifySpockListener extends Specification {

  void "success"() {
    when:

    expect:
    1 == 1
  }

  void "fail"() {
    when:

    expect:
    0 == 1
  }

  @Ignore
  void "skip"() {
    when:

    expect:
    1 == 1
  }

  @Unroll
  void "parameterised (#left ?? #right)"() {
    when:

    expect:
    left == right

    where:
    left | right
    1    | 1
    1    | 2
  }
}
