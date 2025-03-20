package example

import example.endpoints.*
import example.groups.*
import example.utils.*

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

import kotlin.collections.List
import kotlin.collections.toList


class AdvancedSimulation : Simulation() {

    // Define HTTP protocol configuration with authentication header
    // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
    companion object {
        val httpProtocolWithAuthentication = withAuthenticationHeader(
            http.baseUrl(baseUrl)
                .acceptHeader("application/json")
                .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"

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
                                    buy
                                )
                        ),
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
                                    buy
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
                            homeAnonymous,
                            pause(minPauseSec, maxPauseSec),
                            authenticate,
                            homeAuthenticated,
                            pause(minPauseSec, maxPauseSec),
                            addToCart,
                            pause(minPauseSec, maxPauseSec),
                            buy
                        ),
                    group("us")
                        .on(
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
        .exitHereIfFailed()

    // Define different load injection profiles
    // Reference: https://docs.gatling.io/reference/script/core/injection/
    private fun injectionProfile(scn: ScenarioBuilder): PopulationBuilder {
        return when (testType) {
            "capacity" -> scn.injectOpen(
                incrementUsersPerSec(vu.toDouble())
                    .times(4)
                    .eachLevelLasting(duration)
                    .separatedByRampsLasting(4)
                    .startingFrom(10.0)
            )
            "soak" -> scn.injectOpen(constantUsersPerSec(vu.toDouble()).during(duration))
            "stress" -> scn.injectOpen(stressPeakUsers(vu).during(duration))
            "breakpoint" -> scn.injectOpen(rampUsers(vu).during(duration))
            "ramp-hold" -> scn.injectOpen(
                rampUsersPerSec(0.0).to(vu.toDouble()).during(rampDuration),
                constantUsersPerSec(vu.toDouble()).during(duration)
            )
            "smoke" -> scn.injectOpen(atOnceUsers(1))
            else -> scn.injectOpen(atOnceUsers(vu))
        }
    }

    // Define assertions for different test types
    // Reference: https://docs.gatling.io/reference/script/core/assertions/
    private val assertions: List<Assertion> = listOf(
        global().responseTime().percentile(90.0).lt(500),
        global().failedRequests().percent().lt(5.0)
    )

    private fun getAssertions(): List<Assertion> {
        return when (testType) {
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
