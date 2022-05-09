package com.jamf.reporting;

public interface ReportService {

  void sendResults();

  void addResult(TestResult testResult);
}
