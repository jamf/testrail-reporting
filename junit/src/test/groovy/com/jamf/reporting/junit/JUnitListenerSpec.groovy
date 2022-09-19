package com.jamf.reporting.junit

import static com.jamf.reporting.Status.*
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod

import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.TestPlan
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import spock.lang.Specification

import com.jamf.reporting.ReportService
import com.jamf.reporting.ReportServiceProvider
import com.jamf.reporting.TestResult

class JUnitListenerSpec extends Specification {

  def reportService = Mock(ReportService)
  Launcher launcher

  def setup() {
    GroovyMock(ReportServiceProvider, global: true)
    def reportServiceProviderMock = Mock(ReportServiceProvider)
    new ReportServiceProvider() >> reportServiceProviderMock

    _ * ReportServiceProvider.getReportService() >> reportService

    launcher = LauncherFactory.create()
  }

  def "should report results"() {
    given:
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectClass(VerifyJUnitListener))
        .build()

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    12 * reportService.addResult(*_)
    1 * reportService.sendResults()
  }

  def "should report success"() {
    given:
    def request = getRequestForMethod("success")
    def expectedResult = new TestResult(PASS, null, "com.jamf.reporting.junit.VerifyJUnitListener[success]", null)

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report success - custom mapping"() {
    given:
    def request = getRequestForMethod("success_customMatch")
    def expectedResult = new TestResult(PASS, 1234, null, null)

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report success - custom mapping - multiple values"() {
    given:
    def request = getRequestForMethod("success_customMatch_multipleValues")
    def expectedResult = new TestResult(PASS, 1234, null, null)
    def expectedResult2 = new TestResult(PASS, 4321, null, null)

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report failure"() {
    given:
    def request = getRequestForMethod("failTest")
    def expectedResult = new TestResult(FAILED, null, "com.jamf.reporting.junit.VerifyJUnitListener[failTest]", "Should report failed test.")

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report failure - custom mapping"() {
    given:
    def request = getRequestForMethod("failTest_customMatch")
    def expectedResult = new TestResult(FAILED, 1234, null, "Should report failed test.")

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report ignored"() {
    given:
    def request = getRequestForMethod("skip")
    def expectedResult = new TestResult(SKIPPED, null, "com.jamf.reporting.junit.VerifyJUnitListener[skip]", "public void com.jamf.reporting.junit.VerifyJUnitListener.skip() is @Disabled")

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report ignored - custom mapping"() {
    given:
    def request = getRequestForMethod("skip_customMatch")
    def expectedResult = new TestResult(SKIPPED, 1234, null, "public void com.jamf.reporting.junit.VerifyJUnitListener.skip_customMatch() is @Disabled")

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.sendResults()
  }

  def "should report parameterised"() {
    given:
    def request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectMethod(VerifyJUnitListener, "parameterised", "java.lang.String"))
        .build()
    def expectedResult = new TestResult(PASS, null, "com.jamf.reporting.junit.VerifyJUnitListener[parameterised:[1] param 1]", null)
    def expectedResult2 = new TestResult(PASS, null, "com.jamf.reporting.junit.VerifyJUnitListener[parameterised:[2] param 2]", null)

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  def "should report parameterised - custom mapping"() {
    given:
    def request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectMethod(VerifyJUnitListener, "parameterised_customMatch", "java.lang.String"))
        .build()
    def expectedResult = new TestResult(PASS, 1234, null, null)
    def expectedResult2 = new TestResult(PASS, 1234, null, null)

    when:
    TestPlan testPlan = launcher.discover(request)
    launcher.execute(request)

    then:
    1 * reportService.addResult(expectedResult)
    1 * reportService.addResult(expectedResult2)
    1 * reportService.sendResults()
  }

  LauncherDiscoveryRequest getRequestForMethod(String methodName) {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectMethod(VerifyJUnitListener, methodName))
        .build()
    return request
  }
}
