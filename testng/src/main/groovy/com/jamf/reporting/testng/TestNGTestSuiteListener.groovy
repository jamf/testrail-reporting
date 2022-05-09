package com.jamf.reporting.testng

import org.testng.ISuite
import org.testng.ISuiteListener

import com.jamf.reporting.ReportServiceProvider

class TestNGTestSuiteListener implements ISuiteListener {

  @Override
  void onStart(ISuite suite) {
  }

  @Override
  void onFinish(final ISuite suite) {
    ReportServiceProvider.getReportService().sendResults();
  }
}
