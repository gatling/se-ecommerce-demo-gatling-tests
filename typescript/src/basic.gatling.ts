import { atOnceUsers, exec, global, getParameter, scenario, simulation } from "@gatling.io/core";
import { http } from "@gatling.io/http";

import * as api from "./requests/api";
import * as web from "./requests/web";

export default simulation((setUp) => {
  // Load VU count from system properties
  // Reference: https://docs.gatling.io/guides/passing-parameters/
  const vu = parseInt(getParameter("vu", "1"));

  // Define HTTP configuration
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/
  const baseHttpProtocol = http
    .baseUrl("https://api-ecomm.gatling.io")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
    );

  // Define scenario
  // Reference: https://docs.gatling.io/reference/script/core/scenario/
  const scn = scenario("Scenario").exec(
    web.home,
    api.session,
    exec((session) => session.set("pageIndex", 0)),
    api.products
  );

  // Define assertions
  // Reference: https://docs.gatling.io/reference/script/core/assertions/
  const assertion = global().failedRequests().count().lt(1.0);

  // Define injection profile and execute the test
  // Reference: https://docs.gatling.io/reference/script/core/injection/
  setUp(scn.injectOpen(atOnceUsers(vu)))
    .assertions(assertion)
    .protocols(baseHttpProtocol);
});
