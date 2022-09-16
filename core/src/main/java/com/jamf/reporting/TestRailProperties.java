package com.jamf.reporting;

import static com.jamf.reporting.TestRailProperties.Fields.MILESTONE_NAME;
import static com.jamf.reporting.TestRailProperties.Fields.PASSWORD;
import static com.jamf.reporting.TestRailProperties.Fields.PROJECT_ID;
import static com.jamf.reporting.TestRailProperties.Fields.RUN_DESCRIPTION;
import static com.jamf.reporting.TestRailProperties.Fields.RUN_NAME;
import static com.jamf.reporting.TestRailProperties.Fields.SECTION_ID;
import static com.jamf.reporting.TestRailProperties.Fields.SECTION_NAME;
import static com.jamf.reporting.TestRailProperties.Fields.SEND;
import static com.jamf.reporting.TestRailProperties.Fields.SUITE_ID;
import static com.jamf.reporting.TestRailProperties.Fields.SUITE_NAME;
import static com.jamf.reporting.TestRailProperties.Fields.URL;
import static com.jamf.reporting.TestRailProperties.Fields.USERNAME;

import java.util.Properties;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRailProperties {

  private static final Logger logger = LoggerFactory.getLogger(TestRailProperties.class);
  private static final String DEFAULT_MISSING_SECTION_ID_STRING = "-1";
  private static final String DEFAULT_MISSING_SUITE_ID_STRING = "-1";
  private String url;
  private String username;
  private String password;
  private int projectID;
  private Integer sectionID;
  private String sectionName;
  private Integer suiteId;
  private String suiteName;
  private boolean send = false;
  private String runName;
  private String runDescription;
  private String milestoneName;

  TestRailProperties() {
    loadProperties();
    logger.debug(this.toString());
  }

  String getUrl() {
    return url;
  }

  String getUsername() {
    return username;
  }

  String getPassword() {
    return password;
  }

  int getProjectID() {
    return projectID;
  }

  Integer getSectionID() {
    return sectionID == -1 ? null : sectionID;
  }

  String getSectionName() {
    return sectionName;
  }

  public Integer getSuiteId() {
    return suiteId;
  }

  public String getSuiteName() {
    return suiteName;
  }

  boolean isSend() {
    return send;
  }

  String getRunName() {
    return runName;
  }

  String getRunDescription() {
    return runDescription;
  }

  public String getMilestoneName() {
    return milestoneName;
  }

  private void loadProperties() {
    Properties systemProperties = System.getProperties();
    Properties fileProperties = new Properties();
    try {
      fileProperties.load(getClass().getClassLoader().getResourceAsStream("testRail.properties"));
    } catch (Exception e) {
      logger.error("Error reading properties file", e.getMessage());
    }
    try {
      this.url = systemProperties.getProperty(URL.getProperty(), fileProperties.getProperty(URL.getProperty()));
      this.username = systemProperties.getProperty(USERNAME.getProperty(), fileProperties.getProperty(USERNAME.getProperty()));
      this.password = systemProperties.getProperty(PASSWORD.getProperty(), fileProperties.getProperty(PASSWORD.getProperty()));
      this.projectID = Integer.parseInt(systemProperties.getProperty(PROJECT_ID.getProperty(), fileProperties.getProperty(PROJECT_ID.getProperty())));
      this.sectionID = Integer.parseInt(systemProperties.getProperty(SECTION_ID.getProperty(), fileProperties.getProperty(SECTION_ID.getProperty(), DEFAULT_MISSING_SECTION_ID_STRING)));
      this.sectionName = systemProperties.getProperty(SECTION_NAME.getProperty(), fileProperties.getProperty(SECTION_NAME.getProperty()));
      this.suiteId = Integer.parseInt(systemProperties.getProperty(SUITE_ID.getProperty(), fileProperties.getProperty(SUITE_ID.getProperty(), DEFAULT_MISSING_SUITE_ID_STRING)));
      this.suiteName = systemProperties.getProperty(SUITE_NAME.getProperty(), fileProperties.getProperty(SUITE_NAME.getProperty()));
      this.send = Boolean.parseBoolean(systemProperties.getProperty(SEND.getProperty(), fileProperties.getProperty(SEND.getProperty())));
      this.runDescription = systemProperties.getProperty(RUN_DESCRIPTION.getProperty(), fileProperties.getProperty(RUN_DESCRIPTION.getProperty()));
      this.runName = systemProperties.getProperty(RUN_NAME.getProperty(), fileProperties.getProperty(RUN_NAME.getProperty()));
      this.milestoneName = systemProperties.getProperty(MILESTONE_NAME.getProperty(), fileProperties.getProperty(MILESTONE_NAME.getProperty()));
    } catch (Exception e) {
      logger.error("Error reading properties.", e);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", TestRailProperties.class.getSimpleName() + "[", "]")
        .add("url='" + url + "'")
        .add("username='" + username + "'")
        .add("password='*******'")
        .add("projectID=" + projectID)
        .add("sectionID=" + sectionID)
        .add("sectionName=" + sectionName)
        .add("suiteId=" + suiteId)
        .add("suiteName=" + suiteName)
        .add("send=" + send)
        .add("runDescription='" + runDescription + "'")
        .add("runName='" + runName + "'")
        .add("milestoneName='" + milestoneName + "'")
        .toString();
  }

  enum Fields {
    URL("testrail.url"),
    USERNAME("testrail.username"),
    PASSWORD("testrail.password"),
    PROJECT_ID("testrail.projectId"),
    SECTION_ID("testrail.sectionId"),
    SECTION_NAME("testrail.section_name"),
    SUITE_ID("testrail.suiteId"),
    SUITE_NAME("testrail.suite_name"),
    SEND("testrail.send"),
    RUN_DESCRIPTION("testrail.run_description"),
    RUN_NAME("testrail.run_name"),
    MILESTONE_NAME("testrail.milestone_name");

    private String property;

    Fields(String property) {
      this.property = property;
    }

    public String getProperty() {
      return property;
    }
  }
}
