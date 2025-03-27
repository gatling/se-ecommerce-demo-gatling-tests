import { status } from "@gatling.io/http";
import { postman } from "@gatling.io/postman";

// Define the postman collection with its corresponding environment
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#import-collections
const collection = postman
  .fromResource("gatlingEcommerce.postman_collection.json")
  .environment("gatlingEcommerce.postman_environment.json");

// Define the API endpoints by referencing the corresponding folder in the postman collection
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
const apiEndpoints = collection.folder("API Endpoints");

// Define session retrieval endpoint with response validation
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
export const session = apiEndpoints
  .folder("Authentication")
  .request("Create User Session")
  .check(status().is(200));

// Define products endpoint with response validation
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
export const products = apiEndpoints
  .folder("Products")
  .request("Get Products")
  .check(status().is(200));

// Define login request
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
export const login = apiEndpoints
  .folder("Authentication")
  .request("User login")
  .check(status().is(200));

// Define search request
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
export const search = apiEndpoints
  .folder("Products")
  .request("Get Products")
  .check(status().is(200));

// Define the "Add to Cart" request
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
export const cart = apiEndpoints.folder("Cart").request("Add to cart").check(status().is(200));

// Define checkout process
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#create-gatling-requests-and-scenarios
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
export const checkOut = apiEndpoints
  .folder("Checkout")
  .request("Checkout Order")
  .check(status().is(200));
