package com.jamf.reporting;

import com.codepine.api.testrail.TestRail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestRailClientProvider {

  private static final Logger logger = LoggerFactory.getLogger(TestRailClientProvider.class);

  private final String url;
  private final String username;
  private final String password;
  private TestRail client;

  TestRailClientProvider(final TestRailProperties testRailProperties) {
    System.setProperty("jsse.enableSNIExtension", "false");
    url = testRailProperties.getUrl();
    username = testRailProperties.getUsername();
    password = testRailProperties.getPassword();
  }

  TestRail getClient() {
    if (client == null) {
      logger.debug("Creating TestRail client.");
      logger.debug("TestRail URL: {}", this.url);
      logger.debug("TestRail username: {}", this.username);
      client = TestRail.builder(this.url, this.username, this.password).build();
    }
    return client;
  }
}
