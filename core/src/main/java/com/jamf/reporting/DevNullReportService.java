package com.jamf.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevNullReportService implements ReportService {

  private static final Logger logger = LoggerFactory.getLogger(DevNullReportService.class);

  @Override
  public void sendResults() {
    logger.trace("Won't send results to TestRail, because it was not requested.");
  }

  public void addResult(final TestResult testResult) {
    logger.trace("Adding dummy test result.");
  }
}
