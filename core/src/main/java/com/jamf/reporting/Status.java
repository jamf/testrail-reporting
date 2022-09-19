package com.jamf.reporting;

public enum Status {
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