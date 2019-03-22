package com.jamf.reporting;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseField;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.codepine.api.testrail.model.Run;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import junit.framework.TestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRailReportService implements ReportService {

  private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
  private final int projectId;
  private final TestRail testRail;
  private final String testRunName;
  private final String testRunDescription;
  private final List<ResultField> customResultFields;
  private final List<CaseField> caseFields;
  private final int serviceSectionId;
  private Map<String, String> failedTests = new HashMap<>();
  private List<String> skippedTests = new ArrayList<>();
  private List<String> passedTests = new ArrayList<>();
  private List<Case> testRailTestCases;

  TestRailReportService(final TestRailClientProvider clientProvider, TestRailProperties properties) {
    this.testRail = clientProvider.getClient();
    this.projectId = properties.getProjectID();
    this.serviceSectionId = properties.getSectionID();
    this.testRunName = properties.getRunName();
    this.testRunDescription = properties.getRunDescription();
    customResultFields = clientProvider.getClient().resultFields().list().execute();
    caseFields = clientProvider.getClient().caseFields().list().execute();
  }

  @Override
  public void sendResults() {
    logger.debug("Creating Test Rail run with tests that ran.");
    Set<Result> allTests = new HashSet<>();
    Set<Result> failedResults = getFailedResults();
    Set<Result> skippedResults = getSkippedResults();
    Set<Result> passedResults = getPassedResults();

    allTests.addAll(failedResults);
    allTests.addAll(skippedResults);
    allTests.addAll(passedResults);

    Run run = testRail.runs()
        .add(projectId, new Run()
            .setName(this.testRunName)
            .setDescription(this.testRunDescription)
            .setIncludeAll(false)
            .setCaseIds(allTests.stream().map(Result::getCaseId).collect(Collectors.toList())))
        .execute();

    logger.debug("Sending test results to TestRail, to runId {}.", run.getId());

    if (!failedTests.isEmpty()) {
      logger.debug("Sending failed tests to Test Rail.");
      testRail.results().addForCases(run.getId(), new ArrayList<>(failedResults), customResultFields)
          .execute();
    }
    if (!skippedTests.isEmpty()) {
      logger.debug("Sending skipped tests to Test Rail.");
      testRail.results().addForCases(run.getId(), new ArrayList<>(skippedResults), customResultFields)
          .execute();
    }
    if (!passedTests.isEmpty()) {
      logger.debug("Sending passed tests to Test Rail.");
      testRail.results().addForCases(run.getId(), new ArrayList<>(passedResults), customResultFields)
          .execute();
    }
    testRail.runs().close(run.getId())
        .execute();
  }

  @Override
  public void addFail(final String methodName, final String errorMessage) {
    failedTests.put(methodName, errorMessage);
  }

  @Override
  public void addSkip(final String methodName) {
    skippedTests.add(methodName);
  }

  /**
   * Will include test in report, and mark it as <p>PASSED</p>. If <p>methodName</p> is already on list of failed tests, then don't add it, because report will report false positive.
   *
   * @param methodName to be included in report.
   */
  @Override
  public void addPass(final String methodName) {
    if (!failedTests.keySet().contains(methodName)) {
      passedTests.add(methodName);
    }
  }

  /**
   * This method maps each failed test to Test Rail Result. Results contain cause of failure, so it is available in TestRun report.
   *
   * @return Set of Failed Test Results
   */
  private Set<Result> getFailedResults() {
    return failedTests.entrySet().stream().map(s -> new Result().setCaseId(getCaseIdForTest(s.getKey())).setComment(s.getValue()).setStatusId(Status.FAILED.getValue())).collect(Collectors.toSet());
  }

  /**
   * This method maps each skipped test to Test Rail Result.
   *
   * @return Set of Skipped Test Results
   */
  private Set<Result> getSkippedResults() {
    return skippedTests.stream().map(s -> new Result().setCaseId(getCaseIdForTest(s)).setStatusId(Status.SKIPPED.getValue())).collect(Collectors.toSet());
  }

  /**
   * This method maps each passed test to Test Rail Result.
   *
   * @return Set of Passed Test Results
   */
  private Set<Result> getPassedResults() {
    return passedTests.stream().map(s -> new Result().setCaseId(getCaseIdForTest(s)).setStatusId(Status.PASS.getValue())).collect(Collectors.toSet());
  }

  /**
   * Matches test with TestRail test case using <p>testCaseTitle</p>. If can't find one, will create new.
   *
   * @param testCaseTitle used to find matching test case
   * @return TestCaseID of found/created testCase
   */
  private Integer getCaseIdForTest(String testCaseTitle) {
    return getAllTestCasesFromTestRail()
        .stream().filter(c -> c.getTitle().equals(testCaseTitle))
        .findAny().orElseGet(() -> testRail.cases().add(this.serviceSectionId, new Case().setTitle(testCaseTitle), caseFields).execute())
        .getId();
  }

  private List<Case> getAllTestCasesFromTestRail() {
    if (testRailTestCases == null) {
      testRailTestCases = testRail.cases().list(projectId, caseFields).execute();
    }
    return testRailTestCases;
  }

  /**
   * Statuses available in Test Rail, we use only three, rest is here just for reference.
   */
  private enum Status {
    PASS(1),
    BLOCKED(2),
    RETEST(4),
    FAILED(5),
    ERROR(6),
    SKIPPED(7),
    IGNORED(8),
    QA_REVIEW(9),
    IN_PROGRESS(10);

    private final int value;

    Status(final int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

}
