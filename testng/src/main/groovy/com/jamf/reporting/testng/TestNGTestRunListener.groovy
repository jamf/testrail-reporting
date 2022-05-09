package com.jamf.reporting.testng

import static com.jamf.reporting.Status.*

import java.util.function.Consumer
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestNGMethod
import org.testng.ITestResult

import com.jamf.reporting.ReportService
import com.jamf.reporting.ReportServiceProvider
import com.jamf.reporting.Status
import com.jamf.reporting.TestRailTestMatcher
import com.jamf.reporting.TestResult.Builder

class TestNGTestRunListener implements ITestListener {

  private final ReportService reportService

  TestNGTestRunListener() {
    this.reportService = ReportServiceProvider.getReportService()
  }

  @Override
  void onTestSuccess(ITestResult testResult) {
    reportResults(testResult, PASS)
  }

  @Override
  void onTestSkipped(ITestResult testResult) {
    reportResults(testResult, SKIPPED)
  }

  @Override
  void onTestFailure(ITestResult testResult) {
    reportResults(testResult, FAILED)
  }


  @Override
  void onFinish(ITestContext testContext) {
    testContext.getExcludedMethods().each {method ->
      ifMatcherAnnotationPresentOrElse(method,
          {caseId ->
            reportService.addResult(new Builder(IGNORED)
                .withCaseId(caseId)
                .withComment("Disabled")
                .build())
          },
          {
            reportService.addResult(new Builder(IGNORED)
                .withTestCaseTitle(getFullTestName(method))
                .withComment("Disabled")
                .build())
          })
    }
  }

  private reportResults(ITestResult result, Status status) {
    ifMatcherAnnotationPresentOrElse(result.getMethod(),
        {caseId ->
          reportService.addResult(new Builder(status)
              .withCaseId(caseId)
              .withComment(getMessageFromThrowable(result))
              .build())
        },
        {
          reportService.addResult(new Builder(status)
              .withTestCaseTitle(getFullTestName(result))
              .withComment(getMessageFromThrowable(result))
              .build())
        })
  }

  private void ifMatcherAnnotationPresentOrElse(ITestNGMethod testMethod, Consumer<Integer> ifPresent, Runnable ifMissing) {
    def annotations = testMethod.getConstructorOrMethod().getMethod().getAnnotationsByType(TestRailTestMatcher.class)
    if (annotations.size() > 0) {
      annotations.each {it.value().each {ifPresent.accept(it)}}
    } else {
      ifMissing.run()
    }
  }

  private String getFullTestName(ITestNGMethod testNGMethod) {
    "${testNGMethod.getRealClass().getName()}[${testNGMethod.getMethodName()}]"
  }

  private String getFullTestName(ITestResult testResult) {
    def testName = "${testResult.getMethod().getRealClass().getName()}[${testResult.getMethod().getMethodName()}"

    if (testResult.getParameters().length > 0) {
      testName = "${testName}:${testResult.getParameters()[0]}"
    }

    return "${testName}]"
  }

  String getMessageFromThrowable(ITestResult result) {
    if (result.getThrowable() != null) {
      return result.getThrowable().getMessage()
    }
    null
  }
}
