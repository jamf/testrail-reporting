package com.jamf.reporting;

import static com.jamf.reporting.TestRailProperties.Fields.PASSWORD;
import static com.jamf.reporting.TestRailProperties.Fields.PROJECT_ID;
import static com.jamf.reporting.TestRailProperties.Fields.RUN_DESCRIPTION;
import static com.jamf.reporting.TestRailProperties.Fields.RUN_NAME;
import static com.jamf.reporting.TestRailProperties.Fields.SECTION_ID;
import static com.jamf.reporting.TestRailProperties.Fields.SEND;
import static com.jamf.reporting.TestRailProperties.Fields.URL;
import static com.jamf.reporting.TestRailProperties.Fields.USERNAME;

import java.io.IOException;
import java.util.Properties;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRailProperties {

  private static final Logger logger = LoggerFactory.getLogger(TestRailProperties.class);

  private String url;
  private String username;
  private String password;
  private int projectID;
  private int sectionID;
  private boolean send;
  private String runName;
  private String runDescription;

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

  int getSectionID() {
    return sectionID;
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

  private void loadProperties() {
    Properties systemProperties = System.getProperties();
    Properties fileProperties = new Properties();
    try {
      fileProperties.load(getClass().getClassLoader().getResourceAsStream("testRail.properties"));
    } catch (IOException e) {
      logger.error("Error reading properties file", e.getMessage());
    }
    try {
      this.url = systemProperties.getProperty(URL.getProperty(), fileProperties.getProperty(URL.getProperty()));
      this.username = systemProperties.getProperty(USERNAME.getProperty(), fileProperties.getProperty(USERNAME.getProperty()));
      this.password = systemProperties.getProperty(PASSWORD.getProperty(), fileProperties.getProperty(PASSWORD.getProperty()));
      this.projectID = Integer.parseInt(systemProperties.getProperty(PROJECT_ID.getProperty(), fileProperties.getProperty(PROJECT_ID.getProperty())));
      this.sectionID = Integer.parseInt(systemProperties.getProperty(SECTION_ID.getProperty(), fileProperties.getProperty(SECTION_ID.getProperty())));
      this.send = Boolean.parseBoolean(systemProperties.getProperty(SEND.getProperty(), fileProperties.getProperty(SEND.getProperty())));
      this.runDescription = systemProperties.getProperty(RUN_DESCRIPTION.getProperty(), fileProperties.getProperty(RUN_DESCRIPTION.getProperty()));
      this.runName = systemProperties.getProperty(RUN_NAME.getProperty(), fileProperties.getProperty(RUN_NAME.getProperty()));
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
        .add("send=" + send)
        .add("runName='" + runName + "'")
        .add("runDescription='" + runDescription + "'")
        .toString();
  }

  enum Fields {
    URL("testrail.url"),
    USERNAME("testrail.username"),
    PASSWORD("testrail.password"),
    PROJECT_ID("testrail.projectId"),
    SECTION_ID("testrail.sectionId"),
    SEND("testrail.send"),
    RUN_DESCRIPTION("testrail.run_description"),
    RUN_NAME("testrail.run_name");

    private String property;

    Fields(String property) {
      this.property = property;
    }

    public String getProperty() {
      return property;
    }
  }
}
