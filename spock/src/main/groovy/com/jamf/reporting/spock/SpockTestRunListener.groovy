package com.jamf.reporting.spock

import static com.jamf.reporting.Status.*
import static org.spockframework.runtime.model.MethodKind.*

import java.util.function.Consumer
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.ConditionFailedWithExceptionError
import org.spockframework.runtime.model.*

import com.jamf.reporting.ReportService
import com.jamf.reporting.ReportServiceProvider
import com.jamf.reporting.TestRailTestMatcher
import com.jamf.reporting.TestResult.Builder

/**
 * Listener which allows for plugging custom behaviours on various test lifecycle events. If test fails, first <p>error</p> method is invoked, and afterwards ALWAYS <p>afterIteration</p> is invoked
 * (even for tests that are not parameterised).
 */
class SpockTestRunListener extends AbstractRunListener {

  private final ReportService reportService
  private String lastMethodName = "" //Use this to prevent from sending result twice (afterInvocation is also called after error)

  SpockTestRunListener() {
    this.reportService = ReportServiceProvider.getReportService()
  }

  @Override
  void afterIteration(IterationInfo iteration) {
    def currentMethodName = getFullTestName(iteration.getFeature().getFeatureMethod(), iteration.getDisplayName())
    if (currentMethodName != lastMethodName) {
      ifMatcherAnnotationPresentOrElse(iteration.getFeature().getFeatureMethod(),
          {
            reportService.addResult(new Builder(PASS)
                .withCaseId(it)
                .build())
          },
          {
            reportService.addResult(new Builder(PASS)
                .withTestCaseTitle(getFullTestName(iteration.getFeature().getFeatureMethod(), iteration.getDisplayName()))
                .build())
          })
    }

    lastMethodName = currentMethodName
    super.afterIteration(iteration)
  }

  @Override
  void featureSkipped(FeatureInfo feature) {
    def currentMethodName = getFullTestName(feature.getFeatureMethod())
    if (currentMethodName != lastMethodName) {
      ifMatcherAnnotationPresentOrElse(feature.getFeatureMethod(),
          {
            reportService.addResult(new Builder(SKIPPED)
                .withCaseId(it)
                .build())
          },
          {
            reportService.addResult(new Builder(SKIPPED)
                .withTestCaseTitle(getFullTestName(feature.getFeatureMethod()))
                .build())
          }
      )
    }

    lastMethodName = currentMethodName
    super.featureSkipped(feature)
  }

  @Override
  void error(ErrorInfo error) {
    def currentMethodName = getTestCaseNameFromError(error)
    if (currentMethodName != lastMethodName) {
      ifMatcherAnnotationPresentOrElse(error.getMethod(),
          {
            reportService.addResult(new Builder(FAILED)
                .withCaseId(it)
                .withComment(getErrorMessage(error))
                .build())
          },
          {
            reportService.addResult(new Builder(FAILED)
                .withTestCaseTitle(getTestCaseNameFromError(error))
                .withComment(getErrorMessage(error))
                .build())
          })
    }
    lastMethodName = currentMethodName
    super.error(error)
  }

  @Override
  void afterSpec(SpecInfo spec) {
    def skippedFeatures = spec.getAllFeatures().each {f -> f.isSkipped()}
    skippedFeatures.each {feature ->
      {
        def currentMethodName = getFullTestName(feature.getFeatureMethod())
        if (currentMethodName != lastMethodName) {
          ifMatcherAnnotationPresentOrElse(feature.getFeatureMethod(),
              {
                reportService.addResult(new Builder(SKIPPED)
                    .withCaseId(it)
                    .build())
              },
              {
                reportService.addResult(new Builder(SKIPPED)
                    .withTestCaseTitle(getFullTestName(feature.getFeatureMethod()))
                    .build())
              })
        }
        lastMethodName = currentMethodName
      }
    }
  }

  private void ifMatcherAnnotationPresentOrElse(MethodInfo methodInfo, Consumer<Integer> ifPresent, Runnable ifMissing) {
    def annotation
    if (methodInfo.kind in [FEATURE]) {
      annotation = methodInfo.getAnnotation(TestRailTestMatcher)
    } else if (methodInfo.kind in [SETUP]) {
      annotation = methodInfo.getFeature().getFeatureMethod().getAnnotation(TestRailTestMatcher)
    }
    if (annotation != null) {
      annotation.value().each {caseId -> ifPresent.accept(caseId)}
      return
    }
    ifMissing.run()
  }

/**
 * Depending of type of Exception, we have to look for correct error message in various places. This is not a full set of Exceptions, additional ones might need to be added.
 */
  private String getErrorMessage(ErrorInfo error) {
    String errorMessage
    if (error.getException() instanceof NullPointerException) {
      errorMessage = "NullPointerException"
    } else if (error.getException() instanceof ConditionFailedWithExceptionError) {
      errorMessage = ((ConditionFailedWithExceptionError) error.getException()).getCondition().getRendering()
    } else {
      errorMessage = error.getException().getMessage()
    }
    return errorMessage
  }

/**
 * Depending at which stage of test lifecycle error is thrown, we have to look for test method name in different places.
 *
 * In case of setup and cleanup methods it will return the name of actual test method, not the setup method.
 *
 * @param error thrown during test
 * @return test case name to use for reporting
 */
  private String getTestCaseNameFromError(ErrorInfo error) {
    String testCaseName
    MethodKind methodKind = error.getMethod().getKind()
    if (methodKind == FEATURE_EXECUTION) {
      testCaseName = getFullTestName(error.getMethod(), error.getMethod().getFeature().getDisplayName())
    } else if (methodKind == SPEC_EXECUTION) {
      testCaseName = getFullTestName(error.getMethod(), error.getMethod().getParent().getDisplayName())
    } else if (methodKind == SETUP_SPEC || methodKind == CLEANUP_SPEC) {
      testCaseName = getFullTestName(error.getMethod())
    } else if (methodKind == SETUP || methodKind == CLEANUP) {
      testCaseName = getFullTestName(error.getMethod().getFeature().getFeatureMethod())
    } else {
      testCaseName = getFullTestName(error.getMethod(), error.getMethod().getIteration().getDisplayName())
    }
    return testCaseName
  }

  private String getFullTestName(MethodInfo methodInfo) {
    return getFullTestName(methodInfo, methodInfo.getName())
  }

  private String getFullTestName(MethodInfo methodInfo, String methodDisplayName) {
    return methodInfo.getParent().getPackage() + "." + methodInfo.getParent().getName() + "[" + methodDisplayName + "]"
  }

}
