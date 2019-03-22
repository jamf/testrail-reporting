package com.jamf.reporting.testng;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.jamf.reporting.ReportService;
import com.jamf.reporting.ReportServiceProvider;

public class TestNGTestRunListener implements ITestListener {

  private ReportService reportService;

  public TestNGTestRunListener() {
    this.reportService = ReportServiceProvider.getReportService();
  }

  @Override
  public void onTestStart(ITestResult testResult) {
  }

  @Override
  public void onTestSuccess(ITestResult testResult) {
    reportService.addPass(getFullTestName(testResult));
  }

  @Override
  public void onTestFailure(ITestResult testResult) {
    reportService.addFail(getFullTestName(testResult), testResult.getThrowable().getMessage());
  }

  @Override
  public void onTestSkipped(ITestResult testResult) {
    reportService.addSkip(getFullTestName(testResult));
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {
  }

  @Override
  public void onStart(ITestContext testContext) {
  }

  @Override
  public void onFinish(ITestContext testContext) {
  }

  private String getFullTestName(ITestResult testResult) {
    String testName = testResult.getMethod().getRealClass().getName() + "[" + testResult.getMethod().getMethodName();

    if (testResult.getParameters().length > 0) {
      testName = testName + ":" + testResult.getParameters()[0];
    }

    return testName + "]";
  }
}
