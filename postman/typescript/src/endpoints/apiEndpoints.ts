import { status } from "@gatling.io/http";
import { postman } from "@gatling.io/postman";

const collection = postman
  .fromResource("gatlingEcommerce.postman_collection.json")
  .environment("gatlingEcommerce.postman_environment.json");

const apiEndpoints = collection.folder("API Endpoints");

// Define session retrieval endpoint with response validation and data extraction
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
// Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
export const session = apiEndpoints
  .folder("Authentication")
  .request("Create User Session")
  .check(status().is(200));

// Define products endpoint with pagination through query parameters and data extraction
export const products = apiEndpoints
  .folder("Products")
  .request("Get Products")
  .check(status().is(200));

// Define login request
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
export const login = apiEndpoints
  .folder("Authentication")
  .request("User login")
  .check(status().is(200));

// Define search request
export const search = apiEndpoints
  .folder("Products")
  .request("Get Products")
  .check(status().is(200));

// Define the "Add to Cart" request with a JSON payload
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
export const cart = apiEndpoints.folder("Cart").request("Add to cart").check(status().is(200));

// Define checkout process
export const checkOut = apiEndpoints
  .folder("Checkout")
  .request("Checkout Order")
  .check(status().is(200));
