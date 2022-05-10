package com.jamf.reporting;

import java.util.Arrays;

public enum SuiteMode {
  SINGLE_SUITE(1),
  SINGLE_SUITE_WITH_BASELINE(2),
  MULTIPLE_SUITES(3);

  private final int suiteModeId;

  SuiteMode(final int suiteModeId) {
    this.suiteModeId = suiteModeId;
  }

  public int getSuiteModeId() {
    return suiteModeId;
  }

  public static SuiteMode fromSuiteModeId(final int suiteModeId) {
    return Arrays.stream(SuiteMode.values())
        .filter(mode -> suiteModeId == mode.getSuiteModeId())
        .findFirst().orElseThrow(() -> new IllegalArgumentException("No SuiteMode for id=" + suiteModeId));
  }
}
