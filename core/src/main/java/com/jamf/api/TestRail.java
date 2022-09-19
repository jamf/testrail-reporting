package com.jamf.api;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamf.api.config.RequestSpecificationConfig;
import com.jamf.api.models.CaseField;
import com.jamf.api.models.Milestone;
import com.jamf.api.models.Project;

public class TestRail {

  private static final String DEFAULT_BASE_API_PATH = "index.php?/api/v2/";
  private static final Logger logger = LoggerFactory.getLogger(TestRail.class);
  private final RequestSpecification defaultSpec;

  private TestRail(String url, String username, String password) {
    this(RequestSpecificationConfig.getSpecification(username, password, url + DEFAULT_BASE_API_PATH));
  }

  public TestRail(RequestSpecification spec) {
    this.defaultSpec = spec;
  }

  public static class Builder {

    private String username;
    private String password;
    private String uri;

    public Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder setUri(String uri) {
      this.uri = uri;
      return this;
    }

    public TestRail build() {
      ArgValidator.isNotNull(uri);
      ArgValidator.isNotNull(username);
      ArgValidator.isNotNull(password);
      return new TestRail(uri, username, password);
    }

  }

  private Response executeGetRequest(String errorMessage, String restPath) {
    var response = given()
        .spec(defaultSpec)
        .when()
        .get(restPath);
    if (response.getStatusCode() != 200) {
      logger.error(String.format(errorMessage, response.statusCode(), response.getBody().asString()));
      throw new IllegalStateException("There was problem with request!");
    }
    return response;
  }

  private Response executePostRequest(String errorMessage, String restPath, Map<String, Object> requestParam) {
    var response = given()
        .spec(defaultSpec)
        .body(requestParam)
        .when()
        .post(restPath);
    if (response.getStatusCode() != 200) {
      logger.error(String.format(errorMessage, response.statusCode(), response.getBody().asString()));
      throw new IllegalStateException("There was problem with request!");
    }
    return response;
  }

  public Projects projects() {
    return new Projects();
  }

  public CaseFields caseFields() {
    return new CaseFields();
  }

  public Milestones milestones() {
    return new Milestones();
  }

  public class Projects {

    public Project get(final int projectId) {
      ArgValidator.greaterThan(projectId, 0);
      Response response = executeGetRequest("Couldn't get projects received %s with body '%s'", "get_project/" + projectId);
      return response.getBody().as(Project.class);
    }

  }

  public class CaseFields {

    public List<CaseField> list() {
      Response response = executeGetRequest("Couldn't get projects received %s with body '%s'", "get_case_fields");
      return Arrays.stream(response.getBody().as(CaseField[].class)).collect(Collectors.toList());
    }

  }

  public class Milestones {

    public List<Milestone> list(int projectId) {
      Response response = executeGetRequest("Couldn't get milestones received %s with body '%s'", "get_milestones/" + projectId);
      return Arrays.stream(response.getBody().as(Milestone[].class)).collect(Collectors.toList());
    }

    public Milestone add(int projectId, Milestone milestone) {
      HashMap<String, Object> requestParams = new HashMap();
      requestParams.put("project_id", milestone.getProjectId());
      requestParams.put("name", milestone.getName());
      requestParams.put("description", milestone.getDescription());
      requestParams.put("due_on", milestone.getDueOn());
      Response response = executePostRequest("Couldn't get milestones received %s with body '%s'", "add_milestone/" + projectId, requestParams);
      return response.getBody().as(Milestone.class);
    }

    public Milestone update(int milestoneId, Milestone milestone) {
      HashMap<String, Object> requestParams = new HashMap();
      requestParams.put("is_completed", milestone.getCompleted());
      requestParams.put("is_started", milestone.getStarted());
      Response response = executePostRequest("Couldn't get milestones received %s with body '%s'", "update_milestone/" + milestoneId, requestParams);
      return response.getBody().as(Milestone.class);
    }
  }
}
