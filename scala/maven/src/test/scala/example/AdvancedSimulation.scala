package example

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.{ScenarioBuilder, PopulationBuilder}
import example.utils.Config._
import example.groups.ScenarioGroups._
import example.endpoints.APIendpoints.withAuthenticationHeader;


class AdvancedSimulation extends Simulation {

  // Define HTTP protocol configuration with authentication header
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  private val httpProtocolWithAuthentication =  withAuthenticationHeader(
          http.baseUrl(baseUrl)
              .acceptHeader("application/json")
              .userAgentHeader(
                  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"));

  // Define scenario 1 with a random traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
  private val scn1: ScenarioBuilder = scenario("Scenario 1")
    .exitBlockOnFail(
      randomSwitch(
        frPerc -> group("fr")(
            homeAnonymous,
            pause(minPauseSec, maxPauseSec),
            authenticate,
            homeAuthenticated,
            pause(minPauseSec, maxPauseSec),
            addToCart,
            pause(minPauseSec, maxPauseSec),
            buy
        ),
        usPerc -> group("us")(
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
    .exitHereIfFailed

  // Define scenario 2 with a uniform traffic distribution
  // Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
  private val scn2: ScenarioBuilder = scenario("Scenario 2")
    .exitBlockOnFail(
      uniformRandomSwitch(
        group("fr")(
            homeAnonymous,
            pause(minPauseSec, maxPauseSec),
            authenticate,
            homeAuthenticated,
            pause(minPauseSec, maxPauseSec),
            addToCart,
            pause(minPauseSec, maxPauseSec),
            buy
           ),
      group("us")(
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
    .exitHereIfFailed

  // Define different load injection profiles
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  private def injectionProfile(scn: ScenarioBuilder): PopulationBuilder = {
    testType match {
      case "capacity" =>
        scn.inject(
          incrementUsersPerSec(vu)
            .times(4)
            .eachLevelLasting(duration)
            .separatedByRampsLasting(4)
            .startingFrom(10)
        )
      case "soak" =>
        scn.inject(constantUsersPerSec(vu).during(duration))
      case "stress" =>
        scn.inject(stressPeakUsers(vu).during(duration))
      case "breakpoint" =>
        scn.inject(rampUsers(vu).during(duration))
      case "ramp-hold" =>
        scn.inject(
          rampUsersPerSec(0).to(vu).during(rampDuration),
          constantUsersPerSec(vu).during(duration)
        )
      case "smoke" =>
        scn.inject(atOnceUsers(1))
      case _ =>
        scn.inject(atOnceUsers(vu))
    }
  }

  // Define assertions for different test types
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  private def assertions: Seq[Assertion] = Seq(
          global.responseTime.percentile(90.0).lt(500),
          global.failedRequests.percent.lt(5.0)
        )

  private def getAssertions(): Seq[Assertion] = {
    testType match {
      case "capacity" | "soak" | "stress" | "breakpoint" | "ramp-hold" =>
        assertions
      case "smoke" =>
        Seq(global.failedRequests.count.lt(1L))
      case _ => assertions
    }
  }
// Set up the simulation with scenarios, load profiles, and assertions
  setUp(
    injectionProfile(scn1),
    injectionProfile(scn2)
  ).assertions(
    getAssertions(): _*
  ).protocols(httpProtocolWithAuthentication)
}
