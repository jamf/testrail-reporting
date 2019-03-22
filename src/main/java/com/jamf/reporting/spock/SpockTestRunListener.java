package com.jamf.reporting.spock;

import static org.spockframework.runtime.model.MethodKind.FEATURE_EXECUTION;
import static org.spockframework.runtime.model.MethodKind.SPEC_EXECUTION;

import org.spockframework.runtime.AbstractRunListener;
import org.spockframework.runtime.ConditionFailedWithExceptionError;
import org.spockframework.runtime.model.ErrorInfo;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.IterationInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.NodeInfo;

import com.jamf.reporting.ReportService;
import com.jamf.reporting.ReportServiceProvider;

/**
 * Listener which allows for plugging custom behaviours on various test lifecycle events. If test fails, first <p>error</p> method is invoked, and afterwards ALWAYS <p>afterIteration</p> is invoked
 * (even for tests that are not parameterised).
 */
public class SpockTestRunListener extends AbstractRunListener {

  private final ReportService reportService;

  SpockTestRunListener() {
    this.reportService = ReportServiceProvider.getReportService();
  }

  @Override
  public void afterIteration(IterationInfo iteration) {
    reportService.addPass(getFullTestName(iteration.getFeature().getFeatureMethod(), iteration));
    super.afterIteration(iteration);
  }

  @Override
  public void featureSkipped(FeatureInfo feature) {
    reportService.addSkip(getFullTestName(feature.getFeatureMethod()));
    super.featureSkipped(feature);
  }

  @Override
  public void error(ErrorInfo error) {
    reportService.addFail(getTestCaseNameFromError(error), getErrorMessage(error));
    super.error(error);
  }

  /**
   * Depending of type of Exception, we have to look for correct error message in various places. This is not a full set of Exceptions, additional ones might need to be added.
   */
  private String getErrorMessage(ErrorInfo error) {
    String errorMessage;
    if (error.getException() instanceof NullPointerException) {
      errorMessage = "NullPointerException";
    } else if (error.getException() instanceof ConditionFailedWithExceptionError) {
      errorMessage = ((ConditionFailedWithExceptionError) error.getException()).getCondition().getRendering();
    } else {
      errorMessage = error.getException().getMessage();
    }
    return errorMessage;
  }

  /**
   * Depending at which stage of test lifecycle error is thrown, we have to look for test method name in different places.
   *
   * @param error thrown during test
   * @return test case name to use for reporting
   */
  private String getTestCaseNameFromError(ErrorInfo error) {
    String testCaseName;
    if (error.getMethod().getKind() == FEATURE_EXECUTION) {
      testCaseName = getFullTestName(error.getMethod(), error.getMethod().getFeature());
    } else if (error.getMethod().getKind() == SPEC_EXECUTION) {
      testCaseName = getFullTestName(error.getMethod(), error.getMethod().getParent());
    } else {
      testCaseName = getFullTestName(error.getMethod(), error.getMethod().getIteration());
    }
    return testCaseName;
  }

  private String getFullTestName(MethodInfo methodInfo) {
    return getFullTestName(methodInfo, methodInfo);
  }

  private String getFullTestName(MethodInfo methodInfo, NodeInfo nodeInfo) {
    return methodInfo.getDescription().getTestClass().getName() + "[" + nodeInfo.getName() + "]";
  }

}
