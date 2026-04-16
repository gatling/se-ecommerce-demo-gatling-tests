package example.requests;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.config.Config;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import java.util.Optional;

public class Api {

  // Define session retrieval endpoint with response validation and data extraction
  // Reference: https://docs.gatling.io/reference/script/core/checks/#validating
  // Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
  public static final HttpRequestActionBuilder session =
      http("Session")
          .get("/session")
          .check(status().is(200), jmesPath("sessionId").saveAs("sessionId"));

  // Define products endpoint with pagination through query parameters and data extraction
  public static final HttpRequestActionBuilder products =
      http("Product page: #{pageIndex}")
          .get("/products")
          // Dynamically set page index from session using Gatling's Expression Language (EL)
          .queryParam("page", "#{pageIndex}")
          .check(status().is(200));

  // Define login request
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
  public static final HttpRequestActionBuilder login =
      http("Login")
          .post("/login")
          .asFormUrlEncoded()
          .formParam("username", "#{username}")
          .formParam("password", "#{password}")
          .check(status().is(200), jmesPath("accessToken").saveAs("accessToken"));

  // Add authentication header if an access token exists in the session
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/#header
  private static HttpRequestActionBuilder withAuthenticationHeader(
      HttpRequestActionBuilder builder) {
    return builder.header(
        "Authorization",
        session -> Optional.ofNullable(session.getString("accessToken")).orElse(""));
  }

  // Define search request
  public static final HttpRequestActionBuilder search =
      withAuthenticationHeader(
          http("Search")
              .get("/products")
              .queryParam("search", "#{productName}")
              .check(status().is(200), jmesPath("products[0]").saveAs("cartItem")));

  // Define the "Add to cart" request with a JSON payload
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
  public static final HttpRequestActionBuilder addToCart =
      withAuthenticationHeader(
          http("Add to cart")
              .post("/cart")
              .asJson()
              // Load JSON request body from an external file using Gatling's Expression Language
              // (EL) for dynamic values
              .body(ElFileBody(Config.bodies.cart()))
              .check(status().is(200)));

  // Define checkout process
  public static final HttpRequestActionBuilder checkout =
      withAuthenticationHeader(
          http("Checkout")
              .post("/checkout")
              .asJson()
              .body(ElFileBody(Config.bodies.cart()))
              .check(status().is(200)));
}
