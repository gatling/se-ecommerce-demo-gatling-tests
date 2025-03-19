package example

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  // Load VU count from system properties
  // Reference: https://docs.gatling.io/guides/passing-parameters/
  val vu: Int = Integer.getInteger("vu", 1)

  // Define HTTP configuration
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  val httpProtocol = http
    .baseUrl("https://api-ecomm.gatling.io")
    .acceptHeader("application/json")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0")

  // Define scenario
  // Reference: https://docs.gatling.io/reference/script/core/scenario/
  private val scenario1 = scenario("Scenario 1")
    .exec(http("Session").get("/session"))

  // Define assertions
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  private val assertion = global.failedRequests.count.lt(1)

  // Define injection profile and execute the test
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  setUp(
    scenario1.inject(atOnceUsers(vu))
  ).assertions(assertion).protocols(httpProtocol)
}
