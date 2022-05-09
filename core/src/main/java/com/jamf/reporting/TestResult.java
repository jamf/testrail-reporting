package com.jamf.reporting;

import java.util.Objects;

public class TestResult {

  private final Status status;
  private final Integer caseId;
  private final String comment;
  private final String testCaseTitle;

  private TestResult(final Status status, final Integer caseId, final String testCaseTitle, final String comment) {
    this.status = status;
    this.caseId = caseId;
    this.testCaseTitle = testCaseTitle;
    this.comment = comment;
  }

  public Integer getCaseId() {
    return caseId;
  }

  public String getComment() {
    return comment;
  }

  public Status getStatus() {
    return status;
  }

  public String getTestCaseTitle() {
    return testCaseTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestResult)) {
      return false;
    }
    TestResult that = (TestResult) o;
    return getStatus() == that.getStatus() && Objects.equals(getCaseId(), that.getCaseId()) && Objects.equals(getComment(), that.getComment()) && Objects
        .equals(getTestCaseTitle(), that.getTestCaseTitle());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStatus(), getCaseId(), getComment(), getTestCaseTitle());
  }

  public static class Builder {

    private final Status status;
    private Integer caseId;
    private String comment;
    private String testCaseTitle;

    public Builder(Status status) {
      this.status = status;
    }

    public Builder withCaseId(final int caseId) {
      this.caseId = caseId;
      return this;
    }

    public Builder withTestCaseTitle(final String testCaseTitle) {
      this.testCaseTitle = testCaseTitle;
      return this;
    }

    public Builder withComment(final String comment) {
      this.comment = comment;
      return this;
    }

    public TestResult build() {
      return new TestResult(this.status, this.caseId, this.testCaseTitle, this.comment);
    }
  }
}