package com.jamf.reporting.spock

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.spockframework.runtime.extension.ExtensionAnnotation

/**
 * This interface tells spock where to use <p>SpockTestRunListenerExtension.class</p>. Since it's placed on test base class, it will be used with all tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(value = SpockTestRunListenerExtension.class)
@interface ReportSpockResults {

}
