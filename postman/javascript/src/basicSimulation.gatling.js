import { simulation, atOnceUsers, global, scenario, getParameter } from "@gatling.io/core";
import { http } from "@gatling.io/http";
import { postman } from "@gatling.io/postman";

export default simulation((setUp) => {
  // Load VU count from system properties
  // Reference: https://docs.gatling.io/guides/passing-parameters/
  const vu = parseInt(getParameter("vu", "1"));

  // Define HTTP protocol without any configuration
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  // Reference: https://docs.gatling.io/reference/script/protocols/postman/#dsl-overview
  const httpProtocol = http;

  // Define the postman collection with its corresponding environment
  // Reference: https://docs.gatling.io/reference/script/protocols/postman/#import-collections
  const collection = postman
    .fromResource("gatlingEcommerce.postman_collection.json")
    .environment("gatlingEcommerce.postman_environment.json");

  // Define scenario
  // Reference: https://docs.gatling.io/reference/script/core/scenario/
  const scn = scenario("Scenario").exec(
    // Call the authentication endpoint by referencing the corresponding [folder > subfolders > request] in the postman collection
    collection.folder("API Endpoints").folder("Authentication").request("Create User Session")
  );

  // Define assertions
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  const assertion = global().failedRequests().count().lt(1.0);

  // Define injection profile and execute the test
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  setUp(scn.injectOpen(atOnceUsers(vu)))
    .assertions(assertion)
    .protocols(httpProtocol);
});
