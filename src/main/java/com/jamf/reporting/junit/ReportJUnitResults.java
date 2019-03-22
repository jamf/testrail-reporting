package com.jamf.reporting.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@ExtendWith(JUnitTestListener.class)
public @interface ReportJUnitResults {

}
