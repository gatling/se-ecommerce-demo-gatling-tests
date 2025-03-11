package example.endpoints

import example.utils.Keys
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.http.*
import java.util.Optional

object APIendpoints {

    // Add authentication header if an access token exists in the session
    fun withAuthenticationHeader(protocolBuilder: HttpProtocolBuilder): HttpProtocolBuilder {
        return protocolBuilder.header("Authorization") { session ->
            Optional.ofNullable(session.getString(Keys.ACCESS_TOKEN)).orElse("")
        }
    }

    // Define session retrieval endpoint with response validation and data extraction
    val session: HttpRequestActionBuilder = http("Session")
        .get("/session")
        .check(status().`is`(200))
        .check(jmesPath("sessionId").saveAs(Keys.SESSION_ID))

    // Define products endpoint with pagination through query parameters and data extraction
    val products: HttpRequestActionBuilder = http("Product page: #{${Keys.PAGE_INDEX}}")
        .get("/products")
        .queryParam("page", "#{${Keys.PAGE_INDEX}}")
        .check(status().`is`(200))
        .check(jmesPath("products").saveAs(Keys.PRODUCTS))

    // Define login request
    val login: HttpRequestActionBuilder = http("Login")
        .post("/login")
        .asFormUrlEncoded()
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .check(status().`is`(200))
        .check(jmesPath("accessToken").saveAs(Keys.ACCESS_TOKEN))

    // Define search request
    val search: HttpRequestActionBuilder = http("Search")
        .get("/products")
        .queryParam("search", "#{${Keys.PRODUCT_NAME}}")
        .check(status().`is`(200))

    // Define the "Add to Cart" request with a JSON payload
    val cart: HttpRequestActionBuilder = http("Add to Cart")
        .post("/cart")
        .asJson()
        .body(ElFileBody("bodies/cart.json"))
        .check(status().`is`(200))

    // Define checkout process
    val checkOut: HttpRequestActionBuilder = http("Checkout")
        .post("/checkout")
        .asJson()
        .body(ElFileBody("bodies/cart.json"))
        .check(status().`is`(200))
}