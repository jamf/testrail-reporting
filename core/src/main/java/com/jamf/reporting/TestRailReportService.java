package com.jamf.reporting;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseField;
import com.codepine.api.testrail.model.Milestone;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.codepine.api.testrail.model.Run;
import com.codepine.api.testrail.model.Section;
import com.codepine.api.testrail.model.Suite;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRailReportService implements ReportService {

  private static final Logger logger = LoggerFactory.getLogger(TestRailReportService.class);
  private final TestRailProperties testRailProperties;
  private final TestRail testRail;
  private final List<CaseField> caseFields;
  private List<TestResult> testResults = new ArrayList<>();
  private final SuiteMode suiteMode;
  private List<Case> testRailTestCases;
  private boolean alreadyRun = false;

  TestRailReportService(final TestRailClientProvider clientProvider, TestRailProperties testRailProperties) {
    this.testRail = clientProvider.getClient();
    this.testRailProperties = testRailProperties;
    caseFields = clientProvider.getClient().caseFields().list().execute();
    suiteMode = SuiteMode.fromSuiteModeId(this.testRail.projects().get(testRailProperties.getProjectID()).execute().getSuiteMode());
  }

  @Override
  public void sendResults() {
    if (alreadyRun) {
      return;
    }
    logger.debug("Creating Test Rail run with tests that ran.");
    List<Result> allTests = getMappedResults();

    Optional<Run> run = Optional.empty();

    if (!testRailProperties.getMilestoneName().isEmpty()) {
      Optional<Milestone> existingMilestone = getFirstExistingMilestone();
      if (existingMilestone.isEmpty()) {
        logger.debug("Creating milestone [{}]", testRailProperties.getMilestoneName());
        Milestone newMilestone = new Milestone().setName(testRailProperties.getMilestoneName()).setProjectId(testRailProperties.getProjectID()).setCompleted(false);
        newMilestone = testRail.milestones().add(testRailProperties.getProjectID(), newMilestone).execute();

        run = Optional.of(testRail.runs().add(testRailProperties.getProjectID(), getConfiguredRun(testRailProperties.getRunName(), allTests, newMilestone)).execute());
      } else {
        if (existingMilestone.get().isCompleted()) {
          logger.debug("Opening milestone [{}]", testRailProperties.getMilestoneName());
          testRail.milestones().update(existingMilestone.get().setCompleted(false)).execute();
        }

        run = Optional.of(testRail.runs().add(testRailProperties.getProjectID(), getConfiguredRun(testRailProperties.getRunName(), allTests, existingMilestone.get())).execute());
      }
    }

    if (run.isEmpty()) {
      run = Optional.of(testRail.runs().add(testRailProperties.getProjectID(), getConfiguredRun(testRailProperties.getRunName(), allTests)).execute());
    }

    logger.debug("Sending test results to Test Rail, to runId {}.", run.get().getId());

    if (!testResults.isEmpty()) {
      List<ResultField> customResultFields = testRail.resultFields().list().execute();
      testRail.results().addForCases(run.get().getId(), allTests, customResultFields).execute();
    }
    testRail.runs().close(run.get().getId()).execute();
    testRailTestCases = null;
    alreadyRun = true;
  }

  private Optional<Milestone> getFirstExistingMilestone() {
    return testRail.milestones().list(testRailProperties.getProjectID()).execute().stream()
        .filter(milestone1 -> milestone1.getName().equals(testRailProperties.getMilestoneName()))
        .findFirst();
  }

  @Override
  public void addResult(final TestResult testResult) {
    testResults.add(testResult);
  }

  private List<Result> getMappedResults() {
    return testResults.stream()
        .sorted(Comparator.comparing(TestResult::getStatus).reversed())
        .map(s -> {
          var result = new Result();
          if (s.getCaseId() == null) {
            result.setCaseId(getCaseIdForTest(s.getTestCaseTitle()));
          } else {
            result.setCaseId(s.getCaseId());
          }
          return result.setStatusId(s.getStatus().getValue())
              .setComment(s.getComment());
        })
        .collect(Collectors.toList());

  }

  private Run getConfiguredRun(final String suiteName, final List<Result> allTests) {
    return new Run().setName(suiteName).setDescription(testRailProperties.getRunDescription()).setIncludeAll(false).setCaseIds(allTests.stream().map(Result::getCaseId).collect(Collectors.toList()));
  }

  private Run getConfiguredRun(final String suiteName, final List<Result> allTests, Milestone milestone) {
    return new Run().setName(suiteName)
        .setDescription(testRailProperties.getRunDescription())
        .setIncludeAll(false)
        .setCaseIds(allTests.stream().map(Result::getCaseId).collect(Collectors.toList()))
        .setMilestoneId(milestone.getId());
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
        .findAny().orElseGet(() -> testRail.cases().add(getServiceSectionId(), new Case().setTitle(testCaseTitle), caseFields).execute())
        .getId();
  }

  private int getServiceSectionId() {
    if (this.testRailProperties.getSectionID() == null) {
      Section section = testRail.sections().list(this.testRailProperties.getProjectID()).execute()
          .stream().filter(s -> s.getName().equalsIgnoreCase(this.testRailProperties.getSectionName()))
          .findAny().orElseGet(this::createSection);
      return section.getId();
    }
    return this.testRailProperties.getSectionID();
  }

  private Section createSection() {
    var section = new Section().setName(this.testRailProperties.getSectionName());
    if (suiteMode.equals(SuiteMode.MULTIPLE_SUITES)) {
      section.setSuiteId(getSuiteId());
    }
    return testRail.sections().add(this.testRailProperties.getProjectID(), section).execute();
  }

  private Integer getSuiteId() {
    if (this.testRailProperties.getSuiteId() == null) {
      var suite = testRail.suites().list(this.testRailProperties.getProjectID()).execute()
          .stream().filter(s -> s.getName().equalsIgnoreCase(this.testRailProperties.getSuiteName()))
          .findAny().orElseGet(this::createSuite);
      return suite.getId();
    }
    return this.testRailProperties.getSuiteId();
  }

  private Suite createSuite() {
    return testRail.suites().add(this.testRailProperties.getProjectID(), new Suite().setName(this.testRailProperties.getSuiteName())).execute();
  }

  private List<Case> getAllTestCasesFromTestRail() {
    if (testRailTestCases == null) {
      testRailTestCases = testRail.cases().list(testRailProperties.getProjectID(), caseFields).execute();
    }
    return testRailTestCases;
  }
}
