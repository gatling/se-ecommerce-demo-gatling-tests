package example;

import static example.groups.Groups.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.config.Config;
import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.List;

public class AdvancedSimulation extends Simulation {

  // Define HTTP protocol configuration with authentication header
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  static final HttpProtocolBuilder baseHttpProtocol =
      http.baseUrl(Config.urls.api())
          .userAgentHeader(
              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");

  // Define scenario
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
  static final ScenarioBuilder scn =
      scenario("Scenario")
          .exitBlockOnFail()
          .on(
              homeAnonymous,
              pause(Config.parameters.minPauseSec(), Config.parameters.maxPauseSec()),
              authenticate,
              homeAuthenticated,
              pause(Config.parameters.minPauseSec(), Config.parameters.maxPauseSec()),
              cart,
              pause(Config.parameters.minPauseSec(), Config.parameters.maxPauseSec()),
              buy);

  // Define different load injection profiles
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  static PopulationBuilder injectionProfile(ScenarioBuilder scn) {
    return switch (Config.parameters.testType()) {
      case "capacity" ->
          scn.injectOpen(
              incrementUsersPerSec(Config.parameters.vu())
                  .times(4)
                  .eachLevelLasting(Config.parameters.duration())
                  .separatedByRampsLasting(4)
                  .startingFrom(10));
      case "soak" ->
          scn.injectOpen(
              constantUsersPerSec(Config.parameters.vu()).during(Config.parameters.duration()));
      case "stress" ->
          scn.injectOpen(
              stressPeakUsers(Config.parameters.vu()).during(Config.parameters.duration()));
      case "breakpoint" ->
          scn.injectOpen(rampUsers(Config.parameters.vu()).during(Config.parameters.duration()));
      case "ramp-hold" ->
          scn.injectOpen(
              rampUsersPerSec(0)
                  .to(Config.parameters.vu())
                  .during(Config.parameters.rampDuration()),
              constantUsersPerSec(Config.parameters.vu()).during(Config.parameters.duration()));
      case "smoke" -> scn.injectOpen(atOnceUsers(1));
      default -> scn.injectOpen(atOnceUsers(Config.parameters.vu()));
    };
  }

  // Define assertions for different test types
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  static List<Assertion> assertions =
      List.of(
          global().responseTime().percentile(90.0).lt(500),
          global().failedRequests().percent().lt(5.0));

  static List<Assertion> getAssertions() {
    return switch (Config.parameters.testType()) {
      case "capacity", "soak", "stress", "breakpoint", "ramp-hold" -> assertions;
      case "smoke" -> List.of(global().failedRequests().count().lt(1L));
      default -> assertions;
    };
  }

  // Set up the simulation with scenarios, load profiles, and assertions
  {
    setUp(injectionProfile(scn)).assertions(getAssertions()).protocols(baseHttpProtocol);
  }
}
