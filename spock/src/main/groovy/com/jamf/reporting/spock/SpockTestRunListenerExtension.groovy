package com.jamf.reporting.spock

import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

/**
 * This config tells spock what to do when <p>@ReportSpockResults</p> annotation is found on Specification level.
 */
class SpockTestRunListenerExtension implements IAnnotationDrivenExtension<ReportSpockResults> {

  @Override
  void visitSpecAnnotation(ReportSpockResults annotation, SpecInfo spec) {
    spec.addListener(new SpockTestRunListener())
  }
}
