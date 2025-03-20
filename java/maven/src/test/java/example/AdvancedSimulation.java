package example;

import static example.endpoints.APIendpoints.withAuthenticationHeader;
import static example.groups.ScenarioGroups.*;
import static example.utils.Config.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.util.List;

public class AdvancedSimulation extends Simulation {

  // Define HTTP protocol configuration with authentication header
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  static final HttpProtocolBuilder httpProtocolWithAuthentication =
      withAuthenticationHeader(
          http.baseUrl(baseUrl)
              .acceptHeader("application/json")
              .userAgentHeader(
                  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"));

  // Define scenario 1 with a random traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
  static final ScenarioBuilder scn1 =
      scenario("Scenario 1")
          .exitBlockOnFail()
          .on(
              randomSwitch()
                  .on(
                      percent(frPerc)
                          .then(
                              group("fr")
                                  .on(
                                      homeAnonymous,
                                      pause(minPauseSec, maxPauseSec),
                                      authenticate,
                                      homeAuthenticated,
                                      pause(minPauseSec, maxPauseSec),
                                      addToCart,
                                      pause(minPauseSec, maxPauseSec),
                                      buy)),
                      percent(usPerc)
                          .then(
                              group("us")
                                  .on(
                                      homeAnonymous,
                                      pause(minPauseSec, maxPauseSec),
                                      authenticate,
                                      homeAuthenticated,
                                      pause(minPauseSec, maxPauseSec),
                                      addToCart,
                                      pause(minPauseSec, maxPauseSec),
                                      buy))))
          .exitHereIfFailed();

  // Define scenario 2 with a uniform traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
  static final ScenarioBuilder scn2 =
      scenario("Scenario 2")
          .exitBlockOnFail()
          .on(
              uniformRandomSwitch()
                  .on(
                      group("fr")
                          .on(
                              homeAnonymous,
                              pause(minPauseSec, maxPauseSec),
                              authenticate,
                              homeAuthenticated,
                              pause(minPauseSec, maxPauseSec),
                              addToCart,
                              pause(minPauseSec, maxPauseSec),
                              buy),
                      group("us")
                          .on(
                              homeAnonymous,
                              pause(minPauseSec, maxPauseSec),
                              authenticate,
                              homeAuthenticated,
                              pause(minPauseSec, maxPauseSec),
                              addToCart,
                              pause(minPauseSec, maxPauseSec),
                              buy)))
          .exitHereIfFailed();

  // Define different load injection profiles
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  static final PopulationBuilder injectionProfile(ScenarioBuilder scn) {
    return switch (testType) {
      case "capacity" ->
          scn.injectOpen(
              incrementUsersPerSec(vu)
                  .times(4)
                  .eachLevelLasting(duration)
                  .separatedByRampsLasting(4)
                  .startingFrom(10));
      case "soak" -> scn.injectOpen(constantUsersPerSec(vu).during(duration));
      case "stress" -> scn.injectOpen(stressPeakUsers(vu).during(duration));
      case "breakpoint" -> scn.injectOpen(rampUsers(vu).during(duration));
      case "ramp-hold" ->
          scn.injectOpen(
              rampUsersPerSec(0).to(vu).during(ramp_duration),
              constantUsersPerSec(vu).during(duration));
      case "smoke" -> scn.injectOpen(atOnceUsers(1));
      default -> scn.injectOpen(atOnceUsers(vu));
    };
  }

  // Define assertions for different test types
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  static final List<Assertion> assertions =
      List.of(
          global().responseTime().percentile(90.0).lt(500),
          global().failedRequests().percent().lt(5.0));

  static final List<Assertion> getAssertions() {
    return switch (testType) {
      case "capacity", "soak", "stress", "breakpoint", "ramp-hold" -> assertions;
      case "smoke" -> List.of(global().failedRequests().count().lt(1L));
      default -> assertions;
    };
  }

  // Set up the simulation with scenarios, load profiles, and assertions
  {
    setUp(injectionProfile(scn1), injectionProfile(scn2))
        .assertions(getAssertions())
        .protocols(httpProtocolWithAuthentication);
  }
}
