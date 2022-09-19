package com.jamf.reporting.junit

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation

import java.util.function.Consumer
import org.junit.jupiter.api.extension.*
import org.junit.platform.commons.util.StringUtils

import com.jamf.reporting.ReportService
import com.jamf.reporting.ReportServiceProvider
import com.jamf.reporting.Status
import com.jamf.reporting.TestRailTestMatcher
import com.jamf.reporting.TestResult.Builder

class JUnitTestListener implements BeforeAllCallback, AfterAllCallback, AfterTestExecutionCallback, ExecutionCondition {

  private static boolean started = false
  private final ReportService reportService

  JUnitTestListener() {
    reportService = ReportServiceProvider.getReportService()
  }

  @Override
  void afterTestExecution(ExtensionContext context) {
    ifMatcherAnnotationPresentOrElse(context, {caseId ->
      context.getExecutionException()
          .ifPresentOrElse({exception ->
            reportService.addResult(new Builder(Status.FAILED)
                .withCaseId(caseId)
                .withComment(exception.getMessage())
                .build())
          },
              {
                reportService.addResult(new Builder(Status.PASS)
                    .withCaseId(caseId)
                    .build())
              }
          )
    },
        {
          context.getExecutionException()
              .ifPresentOrElse({exception ->
                reportService.addResult(new Builder(Status.FAILED)
                    .withTestCaseTitle(getFullTestName(context))
                    .withComment(exception.getMessage())
                    .build())
              },
                  {
                    reportService.addResult(new Builder(Status.PASS)
                        .withTestCaseTitle(getFullTestName(context))
                        .build())
                  }
              )
        }
    )
  }

  private void ifMatcherAnnotationPresentOrElse(ExtensionContext context, Consumer<Integer> ifPresent, Runnable ifMissing) {
    findAnnotation(context.getElement(), TestRailTestMatcher)
        .ifPresentOrElse(
            {it.value().each {ifPresent.accept(it)}},
            ifMissing
        )
  }

  private String getFullTestName(ExtensionContext context) {
    String testName = context.getTestClass().get().getName() + "[" + context.getTestMethod().get().getName()

    if (context.getTestMethod().get().getParameters().size() > 0) {
      testName = testName + ":" + context.getDisplayName()
    }

    return testName + "]"
  }

  @Override
  void beforeAll(ExtensionContext context) {
    if (!started) {
      started = true
      context.getRoot().getStore(GLOBAL).put(getClass().getName(), this)
    }
  }

  @Override
  void afterAll(ExtensionContext context) {
    reportService.sendResults()
  }

  @Override
  ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

    def element = context.getElement()
    def disabled = findAnnotation(element, Disabled)
    if (disabled.isPresent()) {
      String reason = disabled
          .map({d -> d.value()})
          .filter({v -> StringUtils.isNotBlank(v)})
          .orElseGet({"${element.get()} is @Disabled"})

      ifMatcherAnnotationPresentOrElse(context,
          {caseId ->
            reportService.addResult(new Builder(Status.SKIPPED)
                .withCaseId(caseId)
                .withComment(reason)
                .build())
          },
          {
            reportService.addResult(new Builder(Status.SKIPPED)
                .withTestCaseTitle(getFullTestName(context))
                .withComment(reason)
                .build())
          }
      )

      return ConditionEvaluationResult.disabled(reason)
    }

    return ConditionEvaluationResult.enabled("@Disabled is not present")
  }
}
