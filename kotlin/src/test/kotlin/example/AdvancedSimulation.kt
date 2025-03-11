package example

import example.endpoints.APIendpoints
import example.groups.ScenarioGroups
import example.utils.Config

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
// import java.util.List

import kotlin.collections.List
import kotlin.collections.toList

class AdvancedSimulation : Simulation() {

    // Define HTTP protocol configuration with authentication header
    // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
    companion object {
        val httpProtocolWithAuthentication = APIendpoints.withAuthenticationHeader(
            http.baseUrl(Config.baseUrl)
                .acceptHeader("application/json")
                .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
                )
        )
    }

    // Define scenario 1 with a random traffic distribution
    // Reference: https://docs.gatling.io/reference/script/core/scenario/#randomswitch
    val scn1: ScenarioBuilder = scenario("Scenario 1")
        .exitBlockOnFail()
        .on(
            randomSwitch()
                .on(
                    percent(Config.frPerc)
                        .then(
                            group("fr")
                                .on(
                                    ScenarioGroups.homeAnonymous,
                                    pause(Config.minPauseSec, Config.maxPauseSec),
                                    ScenarioGroups.authenticate,
                                    ScenarioGroups.homeAuthenticated,
                                    pause(Config.minPauseSec, Config.maxPauseSec),
                                    ScenarioGroups.addToCart,
                                    pause(Config.minPauseSec, Config.maxPauseSec),
                                    ScenarioGroups.buy
                                )
                        ),
                    percent(Config.usPerc)
                        .then(
                            group("us")
                                .on(
                                    ScenarioGroups.homeAnonymous,
                                    pause(Config.minPauseSec, Config.maxPauseSec),
                                    ScenarioGroups.authenticate,
                                    ScenarioGroups.homeAuthenticated,
                                    pause(Config.minPauseSec, Config.maxPauseSec),
                                    ScenarioGroups.addToCart,
                                    pause(Config.minPauseSec, Config.maxPauseSec),
                                    ScenarioGroups.buy
                                )
                        )
                )
        )
        .exitHereIfFailed()

    // Define scenario 2 with a uniform traffic distribution
    // Reference: https://docs.gatling.io/reference/script/core/scenario/#uniformrandomswitch
    val scn2: ScenarioBuilder = scenario("Scenario 2")
        .exitBlockOnFail()
        .on(
            uniformRandomSwitch()
                .on(
                    group("fr")
                        .on(
                            ScenarioGroups.homeAnonymous,
                            pause(Config.minPauseSec, Config.maxPauseSec),
                            ScenarioGroups.authenticate,
                            ScenarioGroups.homeAuthenticated,
                            pause(Config.minPauseSec, Config.maxPauseSec),
                            ScenarioGroups.addToCart,
                            pause(Config.minPauseSec, Config.maxPauseSec),
                            ScenarioGroups.buy
                        ),
                    group("us")
                        .on(
                            ScenarioGroups.homeAnonymous,
                            pause(Config.minPauseSec, Config.maxPauseSec),
                            ScenarioGroups.authenticate,
                            ScenarioGroups.homeAuthenticated,
                            pause(Config.minPauseSec, Config.maxPauseSec),
                            ScenarioGroups.addToCart,
                            pause(Config.minPauseSec, Config.maxPauseSec),
                            ScenarioGroups.buy
                        )
                )
        )
        .exitHereIfFailed()

    // Define different load injection profiles
    // Reference: https://docs.gatling.io/reference/script/core/injection/
    private fun injectionProfile(scn: ScenarioBuilder): PopulationBuilder {
        return when (Config.testType) {
            "capacity" -> scn.injectOpen(
                incrementUsersPerSec(Config.vu.toDouble())
                    .times(4)
                    .eachLevelLasting(Config.duration)
                    .separatedByRampsLasting(4)
                    .startingFrom(10.0)
            )
            "soak" -> scn.injectOpen(constantUsersPerSec(Config.vu.toDouble()).during(Config.duration))
            "stress" -> scn.injectOpen(stressPeakUsers(Config.vu).during(Config.duration))
            "breakpoint" -> scn.injectOpen(rampUsers(Config.vu).during(Config.duration))
            "ramp-hold" -> scn.injectOpen(
                rampUsersPerSec(0.0).to(Config.vu.toDouble()).during(Config.rampDuration),
                constantUsersPerSec(Config.vu.toDouble()).during(Config.duration)
            )
            "smoke" -> scn.injectOpen(atOnceUsers(1))
            else -> scn.injectOpen(atOnceUsers(Config.vu))
        }
    }

    // Define assertions for different test types
    // Reference: https://docs.gatling.io/reference/script/core/assertions/
    private val assertions: List<Assertion> = listOf(
        global().responseTime().percentile(90.0).lt(500),
        global().failedRequests().percent().lt(5.0)
    )

    private fun getAssertions(): List<Assertion> {
        return when (Config.testType) {
            "capacity", "soak", "stress", "breakpoint", "ramp-hold" -> assertions
            "smoke" -> listOf(global().failedRequests().count().lt(1L))
            else -> assertions
        }
    }

    // Set up the simulation with scenarios, load profiles, and assertions
    init {
        setUp(
            injectionProfile(scn1), injectionProfile(scn2)
        )
            .assertions(getAssertions())
            .protocols(httpProtocolWithAuthentication)
    }
}
