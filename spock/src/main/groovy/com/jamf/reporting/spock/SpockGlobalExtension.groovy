package com.jamf.reporting.spock

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.IGlobalExtension

import com.jamf.reporting.ReportServiceProvider

/**
 * Custom spock global extension, which at the end of test suite execution sends results to Test Rail.
 */
public class SpockGlobalExtension implements IGlobalExtension {

  private static final Logger logger = LoggerFactory.getLogger(SpockGlobalExtension.class);

  @Override
  void stop() {
    try {
      ReportServiceProvider.getReportService().sendResults()
    } catch (Exception e) {
      logger.error("Could not send results to TestRail.", e)
    }
  }
}
