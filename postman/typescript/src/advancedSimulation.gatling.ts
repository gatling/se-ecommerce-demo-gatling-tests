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
  stressPeakUsers,
  ScenarioBuilder
} from "@gatling.io/core";
import { postman, postmanProtocol } from "@gatling.io/postman";
import {
  collection,
  minPauseSec,
  maxPauseSec,
  frPerc,
  usPerc,
  vu,
  duration,
  ramp_duration,
  testType
} from "./utils/config";
import { addToCart, authenticate, homeAnonymous, homeAuthenticated } from "./groups/scenarioGroups";

export default simulation((setUp) => {
  // Define Postman protocol without any configuration
  // Reference: https://docs.gatling.io/reference/script/protocols/postman/#dsl-overview
  const basePostmanProtocol = postmanProtocol(collection);

  // Define scenario 1 with a random traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
  const scn1 = scenario("Scenario 1")
    .exec(
      // Initialize the Postman scoped variables. This is not automated yet, except when using collection.scenario().
      collection.initVariables,
    )
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
            pause(minPauseSec, maxPauseSec)
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
            pause(minPauseSec, maxPauseSec)
          )
        )
      )
    )
    .exitHereIfFailed();

  // Define scenario 2 with a uniform traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
  const scn2 = scenario("Scenario 2")
    .exec(
      // Initialize the Postman scoped variables. This is not automated yet, expect when using collection.scenario().
      collection.initVariables,
    )
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
          pause(minPauseSec, maxPauseSec)
        ),
        group("us").on(
          homeAnonymous,
          pause(minPauseSec, maxPauseSec),
          authenticate,
          homeAuthenticated,
          pause(minPauseSec, maxPauseSec),
          addToCart,
          pause(minPauseSec, maxPauseSec)
        )
      )
    )
    .exitHereIfFailed();

  // Define different load injection profiles
  // Reference: https://docs.gatling.io/concepts/injection/
  const injectionProfile = (scn: ScenarioBuilder) => {
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
  // Reference: https://docs.gatling.io/concepts/assertions/
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
    .protocols(basePostmanProtocol);
});
