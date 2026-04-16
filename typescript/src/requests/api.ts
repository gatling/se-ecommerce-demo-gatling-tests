import { ElFileBody, Session, jmesPath } from "@gatling.io/core";
import { HttpRequestActionBuilder, http, status } from "@gatling.io/http";

import config from "../config";

// Define session retrieval endpoint with response validation and data extraction
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
// Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
export const session = http("Session")
  .get("/session")
  .check(status().is(200), jmesPath("sessionId").saveAs("sessionId"));

// Define products endpoint with pagination through query parameters and data extraction
export const products = http("Product page: #{pageIndex}")
  .get("/products")
  // Dynamically set page index from session using Gatling's Expression Language (EL)
  .queryParam("page", "#{pageIndex}")
  .check(status().is(200));

// Define login request
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
export const login = http("Login")
  .post("/login")
  .asFormUrlEncoded()
  .formParam("username", "#{username}")
  .formParam("password", "#{password}")
  .check(status().is(200), jmesPath("accessToken").saveAs("accessToken"));

// Add authentication header if an access token exists in the session
// Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/#header
const withAuthorizationHeader = (builder: HttpRequestActionBuilder) =>
  builder.header("Authorization", (session: Session) =>
    session.contains("accessToken") ? session.get<string>("accessToken") : ""
  );

// Define search request
export const search = withAuthorizationHeader(
  http("Search")
    .get("/products")
    .queryParam("search", "#{productName}")
    .check(status().is(200), jmesPath("products[0]").saveAs("cartItem"))
);

// Define the "Add to cart" request with a JSON payload
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
export const addToCart = withAuthorizationHeader(
  http("Add to cart")
    .post("/cart")
    .asJson()
    // Load JSON request body from an external file using Gatling's Expression Language (EL) for dynamic values
    .body(ElFileBody(config.bodies.cart))
    .check(status().is(200))
);

// Define checkout process
export const checkout = withAuthorizationHeader(
  http("Checkout")
    .post("/checkout")
    .asJson()
    .body(ElFileBody(config.bodies.cart))
    .check(status().is(200))
);
