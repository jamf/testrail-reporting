# Overview

Testrail-reporting is a library that provides a common and simplified way of sending results to [TestRail](https://www.gurock.com/testrail).  

The library "attaches" itself on post-test actions and gathers results from tests. At the end of suite results are sent to TestRail.  
Tests are matched with test cases by name.  

Test case format includes FQCN as well as method name and also parameter if needed. Case names can look like:

- `com.jamf.now.qa.reporting.VerifySpockListener[success]`
- `com.jamf.now.qa.reporting.VerifySpockListener[parameterised (1 ?? 1)]`
- `VerifyTestNGListener[parameterised:param1]`
- `VerifyJUnitListener[parameterised:[1] param 1]`

General pattern is: `FQCN[methodName:parameter]`, there are two exceptions:
- Spock parameterised test method name already provides params, so we actually can't use colon as a delimiter
- JUnit parameterised test does not deliver "user friendly" information about parameters, so we have to use `[paramIndex] parameter` format instead

# Contributing

In order to contribute to this project, please read carefully [CONTRIBUTING.md](./CONTRIBUTING.md) first.

# Checkstyle

To scan main sources:

```bash
./gradlew checkstyleMain
```

To scan test sources:

```bash
./gradlew checkstyleTest
```

# Building

```bash
./gradlew build
```

# Deploying to local .m2

```bash
./gradlew publishToMavenLocal
```

or, if you'd like to publish it with custom version

```bash
./gradlew clean build -PcurrentVersion=XXX publishToMavenLocal
```

# Testing

The only way to test listeners is to use `Verify***Listener` classes to debug if all required method invocations happen.

# Using

## Setup

- add gradle dependency  
```groovy
testCompile('com.jamf:testrail-reporting:0.0.1-SNAPSHOT')
```

- add `@ReportResults` annotation
```java
# Spock example
@ReportSpockResults
class BaseATSpec extends Specification { ... }

# JUnit5 example
@ReportJUnitResults
public class BaseAT { ... }

# TestNG example
@Listeners(value = {TestNGTestRunListener.class, TestNGTestSuiteListener.class})
public class BaseAT { ... }
```

## configuration

## testRail.properties
You can use dedicated properties file to configure where test results should be reported.  
Put `testRail.properties` in `test/resources`, and specify below configuration.

```properties
testrail.url= #url to testrail REST api ex: https://sometestrailserver.com/testrail
testrail.username= #used to report results
testrail.password= 
testrail.projectId= #under which results will be saved
testrail.sectionId= #where tests will be created
testrail.send= #should results be sent or not
testrail.run_name= #what should the test run be named, this parameter is required if testrail.send is true
testrail.run_description= #optional description that should be added to test run
```

# Known limitations

Not all features ara available for all test frameworks.  

- TestNG does not allow for wrapping it's `@Listeners` annotation with custom one, that's why native one has to be used
- JUnit 5 does not invoke any callbacks for `@Disabled` (skipped) tests, which means, results for such tests won't be gathered and published
  - In order to workaround this issue use custom [`@Disabled`](./src/main/java/com/jamf/reporting/junit/Disabled.java) annotation.
