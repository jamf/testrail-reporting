package com.jamf.reporting.testng;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.jamf.reporting.ReportServiceProvider;

public class TestNGTestSuiteListener implements ISuiteListener {

  @Override
  public void onStart(ISuite suite) {
  }

  @Override
  public void onFinish(final ISuite suite) {
    ReportServiceProvider.getReportService().sendResults();
  }
}
