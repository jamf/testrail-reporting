package com.jamf.reporting.testng

import static com.jamf.reporting.Status.*

import org.testng.TestNG
import org.testng.xml.XmlClass
import org.testng.xml.XmlInclude
import org.testng.xml.XmlSuite
import org.testng.xml.XmlTest
import spock.lang.Specification
import spock.lang.Unroll

import com.jamf.reporting.ReportService
import com.jamf.reporting.ReportServiceProvider
import com.jamf.reporting.TestResult

class TestNGListenerSpec extends Specification {

  def reportService = Mock(ReportService)
  TestNG testNG

  def setup() {
    GroovyMock(ReportServiceProvider, global: true)
    def reportServiceProviderMock = Mock(ReportServiceProvider)
    new ReportServiceProvider() >> reportServiceProviderMock

    _ * ReportServiceProvider.getReportService() >> reportService

    testNG = new TestNG()
    testNG.addListener(new TestNGTestSuiteListener())
    testNG.addListener(new TestNGTestRunListener())
  }

  def "should report results"() {
    given:
    testNG.setTestClasses(VerifyTestNGListener)

    when:
    testNG.run()

    then:
    14 * reportService.addResult(*_)
    1 * reportService.sendResults()
  }

  def "should report success"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "success")])
    def expectedResult = new TestResult(PASS, null, "com.jamf.reporting.testng.VerifyTestNGListener[success]", null)

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report success - custom mapping"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "success_customMatch")])
    def expectedResult = new TestResult(PASS, 1234, null, null)

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report success - custom mapping - multiple values"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "success_customMatch_multipleValues")])
    def expectedResult = new TestResult(PASS, 1234, null, null)
    def expectedResult2 = new TestResult(PASS, 4321, null, null)

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report failure"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "failTest")])
    def expectedResult = new TestResult(FAILED, null, "com.jamf.reporting.testng.VerifyTestNGListener[failTest]", "Should report failed test.")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report failure - custom mapping"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "failTest_customMatch")])
    def expectedResult = new TestResult(FAILED, 1234, null, "Should report failed test.")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report skip"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "skip")])
    def expectedResult = new TestResult(SKIPPED, null, "com.jamf.reporting.testng.VerifyTestNGListener[skip]", "Skip")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report skip - custom mapping"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "skip_customMatch")])
    def expectedResult = new TestResult(SKIPPED, 1234, null, "Skip")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report disabled"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "success")])

    when:
    testNG.run()

    then:
    12 * reportService.addResult(*_) >> {attributes ->
      attributes.each {
        if (it.testCaseTitle == "com.jamf.reporting.testng.VerifyTestNGListener[success]") {
          assert it.status == PASS
        } else {
          assert it.status == IGNORED
        }
      }
    }
    1 * reportService.sendResults()
  }

  def "should report parameterised"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "parameterised")])
    def expectedResult = new TestResult(PASS, null, "com.jamf.reporting.testng.VerifyTestNGListener[parameterised:param1]", null)
    def expectedResult2 = new TestResult(PASS, null, "com.jamf.reporting.testng.VerifyTestNGListener[parameterised:param2]", null)

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report parameterised - custom mapping"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener, "parameterised_customMatch")])
    def expectedResult = new TestResult(PASS, 1234, null, null)
    def expectedResult2 = new TestResult(PASS, 1234, null, null)

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report setup failure"() {
    given:
    testNG.setTestClasses(VerifyTestNGListener_setup)

    when:
    testNG.run()

    then:
    12 * reportService.addResult(*_)
    1 * reportService.sendResults()
  }

  @Unroll
  def "should report setup failure - #methodName"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener_setup, methodName)])
    def expectedResult = new TestResult(SKIPPED, null, "com.jamf.reporting.testng.VerifyTestNGListener_setup[${methodName}]", "Setup failed!")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()

    where:
    methodName | _
    "success"  | _
    "failTest" | _
    "skip"     | _
  }

  @Unroll
  def "should report setup failure - #methodName - custom mapping"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener_setup, methodName)])
    def expectedResult = new TestResult(SKIPPED, 1234, null, "Setup failed!")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()

    where:
    methodName             | _
    "success_customMatch"  | _
    "failTest_customMatch" | _
    "skip_customMatch"     | _
  }

  def "should report setup failure - success - custom mapping - multiple values"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener_setup, "success_customMatch_multipleValues")])
    def expectedResult = new TestResult(SKIPPED, 1234, null, "Setup failed!")
    def expectedResult2 = new TestResult(SKIPPED, 4321, null, "Setup failed!")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report setup failure - parameterised"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener_setup, "parameterised")])
    def expectedResult = new TestResult(SKIPPED, null, "com.jamf.reporting.testng.VerifyTestNGListener_setup[parameterised:param1]", "Setup failed!")
    def expectedResult2 = new TestResult(SKIPPED, null, "com.jamf.reporting.testng.VerifyTestNGListener_setup[parameterised:param2]", "Setup failed!")

    when:
    testNG.run()

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report setup failure - parameterised - custom mapping"() {
    given:
    testNG.setXmlSuites([getTestSuiteFromClassForMethod(VerifyTestNGListener_setup, "parameterised_customMatch")])
    def expectedResult = new TestResult(SKIPPED, 1234, null, "Setup failed!")

    when:
    testNG.run()

    then:
    2 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  private XmlSuite getTestSuiteFromClassForMethod(clz, methodName) {
    XmlInclude include = new XmlInclude()
    include.setName(methodName)

    XmlClass testClass = new XmlClass()
    testClass.setClass(clz)
    testClass.setIncludedMethods([include])

    XmlSuite suite = new XmlSuite()
    suite.setName("TmpSuite")

    XmlTest test = new XmlTest(suite)
    test.setName("TmpTest")
    test.setXmlClasses(Arrays.asList(testClass))

    return suite
  }
}
