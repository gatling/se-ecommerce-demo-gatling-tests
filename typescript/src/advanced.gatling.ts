import {
  ScenarioBuilder,
  atOnceUsers,
  constantUsersPerSec,
  global,
  incrementUsersPerSec,
  pause,
  rampUsersPerSec,
  scenario,
  simulation,
  stressPeakUsers
} from "@gatling.io/core";
import { http } from "@gatling.io/http";

import { authenticate, buy, cart, homeAnonymous, homeAuthenticated } from "./groups";
import config from "./config";

export default simulation((setUp) => {
  // Define HTTP protocol configuration with authentication header
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  const baseHttpProtocol = http
    .baseUrl(config.urls.api)
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
    );

  // Define scenario
  // Reference: https://docs.gatling.io/reference/script/core/scenario/
  const scn = scenario("Scenario")
    .exitBlockOnFail()
    .on(
      homeAnonymous,
      pause(config.parameters.minPauseSec, config.parameters.maxPauseSec),
      authenticate,
      homeAuthenticated,
      pause(config.parameters.minPauseSec, config.parameters.maxPauseSec),
      cart,
      pause(config.parameters.minPauseSec, config.parameters.maxPauseSec),
      buy
    );

  // Define different load injection profiles
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  const injectionProfile = (scn: ScenarioBuilder) => {
    switch (config.parameters.testType) {
      case "capacity":
        return scn.injectOpen(
          incrementUsersPerSec(config.parameters.vu)
            .times(4)
            .eachLevelLasting(config.parameters.duration)
            .separatedByRampsLasting(4)
            .startingFrom(10)
        );
      case "soak":
        return scn.injectOpen(
          constantUsersPerSec(config.parameters.vu).during(config.parameters.duration)
        );
      case "stress":
        return scn.injectOpen(
          stressPeakUsers(config.parameters.vu).during(config.parameters.duration)
        );
      case "breakpoint":
        return scn.injectOpen(
          rampUsersPerSec(0).to(config.parameters.vu).during(config.parameters.duration)
        );
      case "ramp-hold":
        return scn.injectOpen(
          rampUsersPerSec(0).to(config.parameters.vu).during(config.parameters.rampDuration),
          constantUsersPerSec(config.parameters.vu).during(config.parameters.duration)
        );
      case "smoke":
        return scn.injectOpen(atOnceUsers(1));
      default:
        return scn.injectOpen(atOnceUsers(config.parameters.vu));
    }
  };

  // Define assertions for different test types
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  const assertions = [
    global().responseTime().percentile(90.0).lt(500),
    global().failedRequests().percent().lt(5.0)
  ];

  const getAssertions = () => {
    switch (config.parameters.testType) {
      case "capacity":
      case "soak":
      case "stress":
      case "breakpoint":
      case "ramp-hold":
        return assertions;
      case "smoke":
        return [global().failedRequests().count().lt(1.0)];
      default:
        return assertions;
    }
  };

  // Set up the simulation with scenarios, load profiles, and assertions
  setUp(injectionProfile(scn))
    .assertions(...getAssertions())
    .protocols(baseHttpProtocol);
});
