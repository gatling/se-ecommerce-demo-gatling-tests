import { ElFileBody, jmesPath } from "@gatling.io/core";
import { HttpProtocolBuilder, http, status } from "@gatling.io/http";
import { ACCESS_TOKEN, PAGE_INDEX, PRODUCT_NAME, PRODUCTS, SESSION_ID } from "../utils/keys";

interface Session {
  contains(key: string): boolean;
  get(key: string): string;
}

// Add authentication header if an access token exists in the session
// Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/#header
export function withAuthenticationHeader(protocolBuilder: HttpProtocolBuilder) {
  return protocolBuilder.header("Authorization", (session: Session) =>
    session.contains(ACCESS_TOKEN) ? session.get(ACCESS_TOKEN) : ""
  );
}

// Define session retrieval endpoint with response validation and data extraction
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
// Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
export const session = http("Session")
  .get("/session")
  .check(status().is(200))
  .check(jmesPath("sessionId").saveAs(SESSION_ID));

// Define products endpoint with pagination through query parameters and data extraction
export const products = http(`Product page: #{${PAGE_INDEX}}`)
  .get("/products")
  .queryParam("page", `#{${PAGE_INDEX}}`) // Dynamically set page index from session using Gatling's Expression Language (EL)
  .check(status().is(200))
  .check(jmesPath("products").saveAs(PRODUCTS));

// Define login request
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
export const login = http("Login")
  .post("/login")
  .asFormUrlEncoded()
  .formParam("username", "#{username}")
  .formParam("password", "#{password}")
  .check(status().is(200))
  .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN));

// Define search request
export const search = http("Search")
  .get("/products")
  .queryParam("search", `#{${PRODUCT_NAME}}`)
  .check(status().is(200));

// Define the "Add to Cart" request with a JSON payload
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
export const cart = http("Add to Cart")
  .post("/cart")
  .asJson()
  .body(ElFileBody("bodies/cart.json")) // Load JSON request body from an external file using Gatling's Expression Language (EL) for dynamic values
  .check(status().is(200));

// Define checkout process
export const checkOut = http("Checkout")
  .post("/checkout")
  .asJson()
  .body(ElFileBody("bodies/cart.json"))
  .check(status().is(200));
