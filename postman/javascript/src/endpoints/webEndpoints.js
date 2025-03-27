import { status } from "@gatling.io/http";
import { postman } from "@gatling.io/postman";

// Define the postman collection with its corresponding environment
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#import-collections
const collection = postman
  .fromResource("gatlingEcommerce.postman_collection.json")
  .environment("gatlingEcommerce.postman_environment.json");

// Define the web endpoints by referencing the corresponding folder in the postman collection
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
const webEndpoints = collection.folder("Web Endpoints");

// Define the home page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
export const homePage = webEndpoints.request("Home Page").check(status().in(200, 304)); // Accept both OK (200) and Not Modified (304) statuses

// Define the login page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
export const loginPage = webEndpoints.request("Login Page").check(status().in(200, 304));
