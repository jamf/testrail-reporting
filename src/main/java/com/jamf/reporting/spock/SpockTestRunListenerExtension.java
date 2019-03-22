package com.jamf.reporting.spock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.SpecInfo;

/**
 * This config tells spock what to do when <p>@ReportSpockResults</p> annotation is found on Specification level.
 */
public class SpockTestRunListenerExtension extends AbstractAnnotationDrivenExtension<ReportSpockResults> {

  @Override
  public void visitSpecAnnotation(ReportSpockResults annotation, SpecInfo spec) {
    spec.addListener(new SpockTestRunListener());
  }
}
