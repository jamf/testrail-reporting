package com.jamf.reporting;

public interface ReportService {

  void sendResults();

  void addPass(String methodName);

  void addFail(String methodName, String errorMessage);

  void addSkip(String methodName);
}
