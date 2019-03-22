package com.jamf.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevNullReportService implements ReportService {

  private static final Logger logger = LoggerFactory.getLogger(DevNullReportService.class);

  @Override
  public void sendResults() {
    logger.trace("Won't send results to TestRail, because it was not requested.");
  }

  @Override
  public void addPass(final String methodName) {
    logger.trace("Adding dummy pass results.");
  }

  @Override
  public void addFail(final String methodName, final String errorMessage) {
    logger.trace("Adding dummy fail results.");
  }

  @Override
  public void addSkip(final String methodName) {
    logger.trace("Adding dummy skip results.");
  }
}
