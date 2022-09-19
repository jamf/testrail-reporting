package com.jamf.api

import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response
import static org.mockserver.model.MediaType.JSON_UTF_8

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.parsing.Parser
import io.restassured.specification.RequestSpecification
import org.mockserver.integration.ClientAndServer
import spock.lang.Shared
import spock.lang.Specification

import com.jamf.api.TestRail.Builder
import com.jamf.api.models.CaseField
import com.jamf.api.models.Milestone
import com.jamf.api.models.Project
import com.jamf.reporting.TestRailProperties

class TestRailSpec extends Specification {

  TestRailProperties properties = new TestRailProperties()

  @Shared
  ClientAndServer mockServer
  @Shared
  int mockServerPort = 1666

  def setupSpec() {
    mockServer = ClientAndServer.startClientAndServer(mockServerPort)
  }

  def randomString = {int n ->
    def alphabet = (('A'..'Z') + ('0'..'9')).join()
    new Random().with {
      (1..n).collect {alphabet[nextInt(alphabet.length())]}.join()
    }
  }

  def "should fail with null url"() {
    when: "method is called"
    new Builder().setUri(null).setUsername(randomString(9)).setPassword(randomString(9)).build()

    then: "exception should be thrown"
    thrown IllegalArgumentException
  }

  def "should fail with null username"() {
    when: "method is called"
    new Builder().setUri(randomString(9)).setUsername(null).setPassword(randomString(9)).build()

    then: "exception should be thrown"
    thrown IllegalArgumentException
  }

  def "should fail with null password"() {
    when: "method is called"
    new Builder().setUri(randomString(9)).setUsername(randomString(9)).setPassword(null).build()

    then: "exception should be thrown"
    thrown IllegalArgumentException
  }

  def "should fail with wrong password id"() {
    given:
    def trClient = new Builder().setUri(randomString(9)).setUsername(randomString(9)).setPassword(randomString(9)).build()

    when: "method is called"
    trClient.projects().get(-5)

    then: "exception should be thrown"
    thrown IllegalArgumentException
  }

  def "should return project"() {
    given:
    def name = randomString(10)
    def projectID = properties.getProjectID()
    def project = getProject(projectID, name)
    mockServer
        .when(
            request()
                .withMethod("GET")
                .withPath("/get_project/" + projectID))
        .respond(
            response()
                .withStatusCode(200)
                .withBody(toJson(project), JSON_UTF_8)
        )

    TestRail trClient = new TestRail(getFakeSpecification("http://localhost:" + mockServerPort))

    when:
    def actualProject = trClient.projects().get(projectID)

    then:
    actualProject == project
  }


  def "should return empty result when status code is bad"() {
    given:
    def projectID = 123
    mockServer
        .when(
            request()
                .withMethod("GET")
                .withPath("/get_project/" + 123))
        .respond(
            response()
                .withStatusCode(500)
                .withBody("", JSON_UTF_8)
        )

    TestRail trClient = new TestRail(getFakeSpecification("http://localhost:" + mockServerPort))

    when:
    trClient.projects().get(projectID)

    then:
    def e = thrown(IllegalStateException)
    e.message == "There was problem with request!"
  }

  def "should return case fields"() {
    given:
    def expectedCaseFields = getCaseFields()
    def expectedCaseField = expectedCaseFields.get(0)
    mockServer
        .when(
            request()
                .withMethod("GET")
                .withPath("/get_case_fields"))
        .respond(
            response()
                .withStatusCode(200)
                .withBody(toJson(expectedCaseFields), JSON_UTF_8)
        )

    TestRail trClient = new TestRail(getFakeSpecification("http://localhost:" + mockServerPort))

    when:
    def actualCaseFields = trClient.caseFields().list()
    def caseField = actualCaseFields.get(0)
    then:
    actualCaseFields.size() == expectedCaseFields.size()
    expectedCaseField == caseField
  }

  def "should add milestone"() {
    given:
    def projectId = properties.getProjectID()
    def name = randomString(10)
    def expectedMilestone = new Milestone()
    expectedMilestone.setName(name)
    expectedMilestone.setDescription("description")
    expectedMilestone.setDueOn(new Date())

    mockServer
        .when(
            request()
                .withMethod("POST")
                .withPath("/add_milestone/" + projectId))
        .respond(
            response()
                .withStatusCode(200)
                .withBody(toJson(expectedMilestone), JSON_UTF_8)
        )

    TestRail trClient = new TestRail(getFakeSpecification("http://localhost:" + mockServerPort))

    when:
    def createdMilestone = trClient.milestones().add(projectId, expectedMilestone)
    then:
    expectedMilestone == createdMilestone
  }

  def "should update milestone"() {
    given:
    def milestoneId = 6
    def milestone = getMilestone()
    def expectedMilestone = milestone.setId(milestoneId)

    mockServer
        .when(
            request()
                .withMethod("POST")
                .withPath("/update_milestone/" + milestoneId))
        .respond(
            response()
                .withStatusCode(200)
                .withBody(toJson(expectedMilestone), JSON_UTF_8)
        )

    TestRail trClient = new TestRail(getFakeSpecification("http://localhost:" + mockServerPort))

    when:
    def createdMilestone = trClient.milestones().update(milestoneId, milestone)
    then:
    expectedMilestone == createdMilestone
  }

  private Milestone getMilestone() {
    def expectedMilestone = new Milestone()
    expectedMilestone.setCompleted(false)
    expectedMilestone.setStarted(true)
    expectedMilestone
  }

  def getProject(int projectId, String projectName) {
    def project = new Project()
    project.setId(projectId)
    project.setName(projectName)
    return project
  }

  def getCaseFields() {
    def caseField = new CaseField()
        .setId(1)
        .setDescription("Some Descr")
        .setLabel("Label")
    def caseFields = new ArrayList<CaseField>()
    caseFields.add(caseField)
    return caseFields
  }


  RequestSpecification getFakeSpecification(final String testEndpoint) {
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
    requestSpecBuilder.setBaseUri(testEndpoint)
    RestAssured.defaultParser = Parser.JSON
    return requestSpecBuilder.build()
  }

  def toJson(Object object) {
    ObjectMapper mapper = new ObjectMapper()
    return mapper.writeValueAsString(object)
  }

}
