import { simulation, atOnceUsers, global, scenario, getParameter } from "@gatling.io/core";
import { postman, postmanProtocol } from "@gatling.io/postman";

export default simulation((setUp) => {
  // Load VU count from system properties
  // Reference: https://docs.gatling.io/guides/passing-parameters/
  const vu = parseInt(getParameter("vu", "1"));

  // Define the postman collection with its corresponding environment
  // Reference: https://docs.gatling.io/integrations/postman/#import-collections
  const collection = postman
    .fromResource("gatlingEcommerceScenario.postman_collection.json")
    .environment("gatlingEcommerce.postman_environment.json");

  // Define Postman protocol without any configuration
  // Reference: https://docs.gatling.io/reference/script/protocols/postman/#dsl-overview
  const basePostmanProtocol = postmanProtocol(collection);

  // Define scenario
  // Reference: https://docs.gatling.io/integrations/postman/#create-gatling-requests-and-scenarios
  const scn = collection.scenario("Gatling Ecomm", { recursive: true });

  // Define assertions
  // Reference: https://docs.gatling.io/concepts/assertions/
  const assertion = global().failedRequests().count().lt(1.0);

  // Define injection profile and execute the test
  // Reference: https://docs.gatling.io/concepts/injection/
  setUp(scn.injectOpen(atOnceUsers(vu)))
    .assertions(assertion)
    .protocols(basePostmanProtocol);
});
