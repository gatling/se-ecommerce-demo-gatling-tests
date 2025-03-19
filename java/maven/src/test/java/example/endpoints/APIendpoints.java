package example.endpoints;

import static example.utils.Keys.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.http.*;
import java.util.Optional;

public class APIendpoints {

  // Add authentication header if an access token exists in the session
  // Reference:
  // https://docs.gatling.io/reference/script/protocols/http/protocol/#header
  public static final HttpProtocolBuilder withAuthenticationHeader(
      HttpProtocolBuilder protocolBuilder) {
    return protocolBuilder.header(
        "Authorization",
        session -> Optional.ofNullable(session.getString(ACCESS_TOKEN)).orElse(""));
  }

  // Define session retrieval endpoint with response validation and data
  // extraction
  // Reference: https://docs.gatling.io/reference/script/core/checks/#validating
  // Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
  public static final HttpRequestActionBuilder session =
      http("Session")
          .get("/session")
          .check(status().is(200)) // Validate successful response
          .check(jmesPath("sessionId").saveAs(SESSION_ID)); // Save sessionId from response

  // Define products endpoint with pagination through query parameters and data
  // extraction
  public static final HttpRequestActionBuilder products =
      http("Product page: #{%s}".formatted(PAGE_INDEX))
          .get("/products")
          .queryParam(
              "page",
              "#{%s}"
                  .formatted(PAGE_INDEX)) // Dynamically set page index from session using Gatling's
          // Expression
          // Language (EL)
          .check(status().is(200))
          .check(jmesPath("products").saveAs(PRODUCTS));

  // Define login request
  // Reference:
  // https://docs.gatling.io/reference/script/protocols/http/request/#forms
  public static final HttpRequestActionBuilder login =
      http("Login")
          .post("/login")
          .asFormUrlEncoded() // Short for header("Content-Type",
          // "application/x-www-form-urlencoded")
          .formParam("username", "#{username}")
          .formParam("password", "#{password}")
          .check(status().is(200))
          .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN));

  // Define search request
  public static final HttpRequestActionBuilder search =
      http("Search")
          .get("/products")
          .queryParam(
              "search",
              "#{%s}"
                  .formatted(
                      PRODUCT_NAME)) // Dynamically set product name from session using Gatling's
          // Expression
          // Language (EL)
          .check(status().is(200));

  // Define the "Add to Cart" request with a JSON payload
  // Reference:
  // https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
  public static final HttpRequestActionBuilder cart =
      http("Add to Cart")
          .post("/cart")
          .asJson()
          .body(
              ElFileBody("bodies/cart.json")) // Load JSON request body from an external file using
          // Gatling's Expression Language (EL) for dynamic values
          .check(status().is(200));

  // Define checkout process
  public static final HttpRequestActionBuilder checkOut =
      http("Checkout")
          .post("/checkout")
          .asJson()
          .body(ElFileBody("bodies/cart.json"))
          .check(status().is(200));
}
