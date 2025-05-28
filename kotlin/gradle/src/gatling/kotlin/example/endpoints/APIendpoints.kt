package example.endpoints

import example.utils.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.*
import io.gatling.javaapi.http.HttpDsl.*
import java.util.Optional

// Add authentication header if an access token exists in the session
// Reference: https://docs.gatling.io/reference/script/protocols/http/protocol/#header
fun withAuthenticationHeader(protocolBuilder: HttpProtocolBuilder): HttpProtocolBuilder {
  return protocolBuilder.header("Authorization") { session ->
    Optional.ofNullable(session.getString(ACCESS_TOKEN)).orElse("")
  }
}

// Define session retrieval endpoint with response validation and data extraction
// Reference: https://docs.gatling.io/reference/script/core/checks/#validating
// Reference: https://docs.gatling.io/reference/script/core/checks/#extracting
val session: HttpRequestActionBuilder =
    http("Session")
        .get("/session")
        .check(status().`is`(200))
        .check(jmesPath("sessionId").saveAs(SESSION_ID))

// Define products endpoint with pagination through query parameters and data extraction
val products: HttpRequestActionBuilder =
    http("Product page: #{${PAGE_INDEX}}")
        .get("/products")
        .queryParam("page", "#{${PAGE_INDEX}}")
        .check(status().`is`(200))
        .check(jmesPath("products").saveAs(PRODUCTS))

// Define login request
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#forms
val login: HttpRequestActionBuilder =
    http("Login")
        .post("/login")
        .asFormUrlEncoded()
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .check(status().`is`(200))
        .check(jmesPath("accessToken").saveAs(ACCESS_TOKEN))

// Define search request
val search: HttpRequestActionBuilder =
    http("Search")
        .get("/products")
        .queryParam("search", "#{${PRODUCT_NAME}}")
        .check(status().`is`(200))

// Define the "Add to Cart" request with a JSON payload
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#elfilebody
val cart: HttpRequestActionBuilder =
    http("Add to Cart")
        .post("/cart")
        .asJson()
        .body(ElFileBody("bodies/cart.json"))
        .check(status().`is`(200))

// Define checkout process
val checkOut: HttpRequestActionBuilder =
    http("Checkout")
        .post("/checkout")
        .asJson()
        .body(ElFileBody("bodies/cart.json"))
        .check(status().`is`(200))
