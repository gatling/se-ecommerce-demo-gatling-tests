package example.config;

import static io.gatling.javaapi.core.CoreDsl.*;

import io.gatling.javaapi.core.FeederBuilder;

public class Feeders {

  // Define a feeder for user data
  // Reference: https://docs.gatling.io/reference/script/core/feeder/
  public static final FeederBuilder<Object> users = jsonFile(Config.feeders.users()).circular();
  public static final FeederBuilder<String> products = csv(Config.feeders.products()).circular();
}
