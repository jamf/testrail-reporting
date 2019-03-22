package com.jamf.reporting.junit;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.StringUtils;

import com.jamf.reporting.ReportService;
import com.jamf.reporting.ReportServiceProvider;

public class JUnitTestListener implements BeforeAllCallback, AfterTestExecutionCallback, ExtensionContext.Store.CloseableResource, ExecutionCondition {

  private static boolean started = false;
  private final ReportService reportService;

  JUnitTestListener() {
    reportService = ReportServiceProvider.getReportService();
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    if (context.getExecutionException().isPresent()) {
      reportService.addFail(getFullTestName(context), context.getExecutionException().get().getMessage());
    } else {
      reportService.addPass(getFullTestName(context));
    }
  }

  private String getFullTestName(ExtensionContext context) {
    String testName = context.getTestClass().get().getName() + "[" + context.getTestMethod().get().getName();

    if (context.getTestMethod().get().getParameters().length > 0) {
      testName = testName + ":" + context.getDisplayName();
    }

    return testName + "]";
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    if (!started) {
      started = true;
      context.getRoot().getStore(GLOBAL).put(getClass().getName(), this);
    }
  }

  @Override
  public void close() {
    reportService.sendResults();
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

    Optional<AnnotatedElement> element = context.getElement();
    Optional<Disabled> disabled = findAnnotation(element, Disabled.class);
    if (disabled.isPresent()) {
      reportService.addSkip(getFullTestName(context));

      String reason = disabled
          .map(Disabled::reason)
          .filter(StringUtils::isNotBlank)
          .orElseGet(() -> element.get() + " is @Disabled");
      return ConditionEvaluationResult.disabled(reason);
    }

    return ConditionEvaluationResult.enabled("@Disabled is not present");
  }
}
