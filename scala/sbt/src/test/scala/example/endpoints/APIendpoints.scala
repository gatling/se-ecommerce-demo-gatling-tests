package example.endpoints

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import example.utils.Keys._

object APIendpoints {

  // Add authentication header if an access token exists in the session
  // Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/#header
  def withAuthenticationHeader(protocolBuilder: HttpProtocolBuilder): HttpProtocolBuilder = {
    protocolBuilder.header(
      "Authorization",
      session => if (session.contains(ACCESS_TOKEN)) session(ACCESS_TOKEN).as[String] else ""
    )
  }

  // Define session retrieval endpoint with response validation and data extraction
  // Reference: https://docs.gatling.io/reference/script/core/checks/#validating
  // Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
  val session = http("Session")
    .get("/session")
    .check(status.is(200))
    .check(jmesPath("sessionId").saveAs(SESSION_ID))

  // Define products endpoint with pagination through query parameters and data extraction
  val products = http(s"Product page: #{$PAGE_INDEX}")
    .get("/products")
    .queryParam("page", s"#{$PAGE_INDEX}")
    .check(status.is(200))
    .check(jmesPath("products").saveAs(PRODUCTS))

  // Define login request
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
  val login = http("Login")
    .post("/login")
    .asFormUrlEncoded
    .formParam("username", "#{username}")
    .formParam("password", "#{password}")
    .check(status.is(200))
    .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN))

  // Define search request
  val search = http("Search")
    .get("/products")
    .queryParam("search", s"#{$PRODUCT_NAME}")
    .check(status.is(200))

  // Define the "Add to Cart" request with a JSON payload
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
  val cart = http("Add to Cart")
    .post("/cart")
    .asJson
    .body(ElFileBody("bodies/cart.json")) // Load JSON request body from an external file using Gatling's Expression Language (EL) for dynamic values
    .check(status.is(200))

  // Define checkout process
  val checkOut = http("Checkout")
    .post("/checkout")
    .asJson
    .body(ElFileBody("bodies/cart.json"))
    .check(status.is(200))
}
