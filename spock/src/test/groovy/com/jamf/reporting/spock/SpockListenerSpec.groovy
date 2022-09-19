package com.jamf.reporting.spock

import static com.jamf.reporting.Status.*

import spock.lang.Ignore
import spock.lang.Specification
import spock.util.EmbeddedSpecRunner

import com.jamf.reporting.ReportService
import com.jamf.reporting.ReportServiceProvider
import com.jamf.reporting.TestResult

class SpockListenerSpec extends Specification {

  def reportService = Mock(ReportService)
  EmbeddedSpecRunner runner

  def setup() {
    GroovyMock(ReportServiceProvider, global: true)
    def reportServiceProviderMock = Mock(ReportServiceProvider)
    new ReportServiceProvider() >> reportServiceProviderMock

    _ * ReportServiceProvider.getReportService() >> reportService

    runner = new EmbeddedSpecRunner(throwFailure: false)
  }

  @Ignore("This test is ignored until we figure out how to report it properly")
  def "should report results"() {
    when:
    runner.runClass(VerifySpockListener)

    then:
    12 * reportService.addResult(*_)
    2 * reportService.sendResults() // Twice, because SpockGlobalExtension is loaded twice... once because we use spock to run tests, second one, because we test how spock behaves
  }

  def "should report success"() {
    given:
    String bodySrc = """
    void "success"() {
        expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(PASS, null, "com.jamf.reporting.spock.MySpec[success]", null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report success - custom mapping"() {
    given:
    String bodySrc = """
    @TestRailTestMatcher(1234)
    void "success - custom matching"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(PASS, 1234, null, null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report success - custom mapping - multiple values"() {
    given:
    String bodySrc = """
    @TestRailTestMatcher([1234, 4321])
    void "success - custom matching - multiple values"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(PASS, 1234, null, null)
    def expectedResult2 = new TestResult(PASS, 4321, null, null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    2 * reportService.sendResults()
  }

  def "should report failure"() {
    given:
    String bodySrc = """
    void "fail"() {
      expect: 0 == 1
    }
    """

    def expectedResult = new TestResult(FAILED, null, "com.jamf.reporting.spock.MySpec[fail]", "Condition not satisfied:\n" +
        "\n" +
        "0 == 1\n" +
        "  |\n" +
        "  false\n")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report failure - custom mapping"() {
    given:
    String bodySrc = """
    @TestRailTestMatcher(1234)
    void "fail - custom matching"() {
      expect: 0 == 1
    }
    """

    def expectedResult = new TestResult(FAILED, 1234, null, "Condition not satisfied:\n" +
        "\n" +
        "0 == 1\n" +
        "  |\n" +
        "  false\n")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report ignored"() {
    given:
    String bodySrc = """
    @Ignore
    void "skip"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(SKIPPED, null, "com.jamf.reporting.spock.MySpec[skip]", null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report ignored - custom mapping"() {
    given:
    String bodySrc = """
    @Ignore
    @TestRailTestMatcher(1234)
    void "skip - custom matching"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(SKIPPED, 1234, null, null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report parameterised"() {
    given:
    String bodySrc = """
    @Unroll
    void "parameterised (#left ?? #right)"() {
      expect:
      left == right
  
      where:
      left | right
      1    | 1
      1    | 2
    }
    """

    def expectedResult = new TestResult(PASS, null, "com.jamf.reporting.spock.MySpec[parameterised (1 ?? 1)]", null)
    def expectedResult2 = new TestResult(FAILED, null, "com.jamf.reporting.spock.MySpec[parameterised (1 ?? 2)]", "Condition not satisfied:\n" +
        "\n" +
        "left == right\n" +
        "|    |  |\n" +
        "1    |  2\n" +
        "     false\n")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    2 * reportService.sendResults()
  }

  def "should report parameterised - custom mapping"() {
    given:
    String bodySrc = """
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
    """

    def expectedResult = new TestResult(PASS, 1234, null, null)
    def expectedResult2 = new TestResult(PASS, 1234, null, null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    2 * reportService.sendResults()
  }

  @Ignore("This test is ignored until we figure out how to report it properly")
  def "should report setup failure"() {
    when:
    runner.runClass(VerifySpockListener_setup)

    then:
    5 * reportService.addResult(*_) //TODO change to "6 *" when issue with parameterised tests is fixed
    2 * reportService.sendResults()
  }

  def "should report failure if setup fails"() {
    given:
    String bodySrc = """
    def setup(){
      throw new RuntimeException("Setup failed.")
    }
    
    void "success"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(FAILED, null, "com.jamf.reporting.spock.MySpec[success]", "Setup failed.")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report failure if setup fails - failure"() {
    given:
    String bodySrc = """
    def setup(){
      throw new RuntimeException("Setup failed.")
    }
    
    void "fail"() {
      expect: 0 == 1
    }
    """

    def expectedResult = new TestResult(FAILED, null, "com.jamf.reporting.spock.MySpec[fail]", "Setup failed.")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should report failure if setup fails - custom mapper"() {
    given:
    String bodySrc = """
    def setup(){
      throw new RuntimeException("Setup failed.")
    }
    
    @TestRailTestMatcher(1234)
    void "success - custom matching"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(FAILED, 1234, null, "Setup failed.")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  def "should not report failure if setup fails - ignored"() {
    given:
    String bodySrc = """
    def setup(){
      throw new RuntimeException("Setup failed.")
    }
    
    @Ignore
    void "skip"() {
      expect: 1 == 1
    }
    """

    def expectedResult = new TestResult(SKIPPED, null, "com.jamf.reporting.spock.MySpec[skip]", null)

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    2 * reportService.sendResults()
  }

  @Ignore("This test is ignored until we figure out how to report it properly")
  def "should report failure if setup fails - parameterised"() {
    given:
    String bodySrc = """
    def setup(){
      throw new RuntimeException("Setup failed.")
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
    """

    def expectedResult = new TestResult(FAILED, null, "com.jamf.reporting.spock.MySpec[parameterised (1 ?? 1)]", "Setup failed.")
    def expectedResult2 = new TestResult(FAILED, null, "com.jamf.reporting.spock.MySpec[parameterised (2 ?? 2)]", "Setup failed.")

    when:
    runner.run(getTestSource(bodySrc))

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    2 * reportService.sendResults()
  }

  String getTestSource(String methodBody) {
    """
    package com.jamf.reporting.spock
    
    import spock.lang.Ignore
    import spock.lang.Specification
    import spock.lang.Unroll

    import com.jamf.reporting.TestRailTestMatcher

    @ReportSpockResults
    class MySpec extends Specification { ${methodBody.trim() + '\n'} }
    """
  }
}
