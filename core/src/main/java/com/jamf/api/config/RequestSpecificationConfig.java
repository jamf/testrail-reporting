package com.jamf.api.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestSpecificationConfig {

  private static final Logger logger = LoggerFactory.getLogger(RequestSpecificationConfig.class);

  public static RequestSpecification getSpecification(String username, String password, String baseUri) {
    logger.info("Setting BaseURI as '" + baseUri + "'");
    RestAssured.defaultParser = Parser.JSON;
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder.setBaseUri(sanitizeUri(baseUri));
    requestSpecBuilder.addHeader("Accept", "application/json");
    requestSpecBuilder.addHeader("Content-Type", "application/json");
    requestSpecBuilder.addHeader("Authorization", basicAuth(username, password));
    return requestSpecBuilder.build();
  }

  private static String sanitizeUri(String baseUri) {
    String sanitizedUri = baseUri;
    if (sanitizedUri.isEmpty() || !sanitizedUri.endsWith("/")) {
      sanitizedUri = sanitizedUri.concat("/");
    }
    return sanitizedUri;
  }

  private static String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }
}
