import {
  simulation,
  scenario,
  group,
  randomSwitch,
  uniformRandomSwitch,
  percent,
  pause,
  global,
  atOnceUsers,
  constantUsersPerSec,
  incrementUsersPerSec,
  rampUsersPerSec,
  stressPeakUsers
} from "@gatling.io/core";
import { http } from "@gatling.io/http";
import {
  minPauseSec,
  maxPauseSec,
  baseUrl,
  frPerc,
  usPerc,
  vu,
  duration,
  ramp_duration,
  testType
} from "./utils/config";
import { withAuthenticationHeader } from "./endpoints/apiEndpoints";
import {
  addToCart,
  authenticate,
  buy,
  homeAnonymous,
  homeAuthenticated
} from "./groups/scenarioGroups";

export default simulation((setUp) => {
  // Define HTTP protocol configuration with authentication header
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  const httpProtocol = withAuthenticationHeader(
    http
      .baseUrl(baseUrl)
      .acceptHeader("application/json")
      .userAgentHeader(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
      )
  );

  // Define scenario 1 with a random traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
  const scn1 = scenario("Scenario 1")
    .exitBlockOnFail()
    .on(
      randomSwitch().on(
        percent(frPerc).then(
          group("fr").on(
            homeAnonymous,
            pause(minPauseSec, maxPauseSec),
            authenticate,
            homeAuthenticated,
            pause(minPauseSec, maxPauseSec),
            addToCart,
            pause(minPauseSec, maxPauseSec),
            buy
          )
        ),
        percent(usPerc).then(
          group("us").on(
            homeAnonymous,
            pause(minPauseSec, maxPauseSec),
            authenticate,
            homeAuthenticated,
            pause(minPauseSec, maxPauseSec),
            addToCart,
            pause(minPauseSec, maxPauseSec),
            buy
          )
        )
      )
    )
    .exitHereIfFailed();

  // Define scenario 2 with a uniform traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
  const scn2 = scenario("Scenario 2")
    .exitBlockOnFail()
    .on(
      uniformRandomSwitch().on(
        group("fr").on(
          homeAnonymous,
          pause(minPauseSec, maxPauseSec),
          authenticate,
          homeAuthenticated,
          pause(minPauseSec, maxPauseSec),
          addToCart,
          pause(minPauseSec, maxPauseSec),
          buy
        ),
        group("us").on(
          homeAnonymous,
          pause(minPauseSec, maxPauseSec),
          authenticate,
          homeAuthenticated,
          pause(minPauseSec, maxPauseSec),
          addToCart,
          pause(minPauseSec, maxPauseSec),
          buy
        )
      )
    )
    .exitHereIfFailed();

  // Define different load injection profiles
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  const injectionProfile = (scn) => {
    switch (testType) {
      case "capacity":
        return scn.injectOpen(
          incrementUsersPerSec(vu)
            .times(4)
            .eachLevelLasting({ amount: duration, unit: "minutes" })
            .separatedByRampsLasting(4)
            .startingFrom(10)
        );
      case "soak":
        return scn.injectOpen(
          constantUsersPerSec(vu).during({ amount: duration, unit: "minutes" })
        );
      case "stress":
        return scn.injectOpen(stressPeakUsers(vu).during({ amount: duration, unit: "minutes" }));
      case "breakpoint":
        return scn.injectOpen(
          rampUsersPerSec(0).to(vu).during({ amount: duration, unit: "minutes" })
        );
      case "ramp-hold":
        return scn.injectOpen(
          rampUsersPerSec(0).to(vu).during({ amount: ramp_duration, unit: "minutes" }),
          constantUsersPerSec(vu).during({ amount: duration, unit: "minutes" })
        );
      case "smoke":
        return scn.injectOpen(atOnceUsers(1));
      default:
        return scn.injectOpen(atOnceUsers(vu));
    }
  };

  // Define assertions for different test types
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  const assertions = [
    global().responseTime().percentile(90.0).lt(500),
    global().failedRequests().percent().lt(5.0)
  ];

  const getAssertions = () => {
    switch (testType) {
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
  setUp(injectionProfile(scn1), injectionProfile(scn2))
    .assertions(...getAssertions())
    .protocols(httpProtocol);
});
