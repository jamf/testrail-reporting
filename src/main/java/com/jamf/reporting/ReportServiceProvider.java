package com.jamf.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportServiceProvider {

  private static final Logger logger = LoggerFactory.getLogger(ReportServiceProvider.class);

  private static TestRailProperties properties;
  private static ReportService reportService;

  private static boolean shouldSendResults;

  static {
    properties = new TestRailProperties();
    shouldSendResults = shouldSendResults();
  }

  public static ReportService getReportService() {
    if (shouldSendResults && reportService == null) {
      logger.debug("Returning Test Rail Service, going to send results to TestRail.");
      TestRailClientProvider config = new TestRailClientProvider(properties);
      reportService = new TestRailReportService(config, properties);
    } else if (!shouldSendResults && reportService == null) {
      logger.trace("Reporting test results was not requested.");
      reportService = new DevNullReportService();
    }
    return reportService;
  }

  /**
   * Determines if we should send results to Test Rail or not. We should only send results if <p>tool.testrail.send</p> is true and <p>tool.testrail.run_name</p> is not empty.
   */
  private static boolean shouldSendResults() {
    if (properties.isSend() && properties.getRunName().isEmpty()) {
      logger.error("Cannot send results without providing test run name.");
    }
    return properties.isSend() && !properties.getRunName().isEmpty();
  }
}
