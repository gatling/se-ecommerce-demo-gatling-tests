import { http, status } from "@gatling.io/http";

import config from "../config";

// Define the home page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
export const home = http("Home page")
  .get(config.urls.web)
  .header("Accept", "text/html")
  // Accept both OK (200) and Not Modified (304) statuses
  .check(status().in(200, 304));

// Define the login page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
export const login = http("Login page")
  .get(`${config.urls.web}/login`)
  .header("Accept", "text/html")
  .check(status().in(200, 304));
