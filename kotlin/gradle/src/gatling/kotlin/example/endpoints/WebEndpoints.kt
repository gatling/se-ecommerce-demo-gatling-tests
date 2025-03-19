package example.endpoints

import example.utils.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.http.HttpRequestActionBuilder

// Define the home page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
val homePage: HttpRequestActionBuilder = http("HomePage")
    .get(pageUrl)
    .check(status().`in`(200, 304)) // Accept both OK (200) and Not Modified (304) statuses

// Define the login page request with response status validation
// Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
val loginPage: HttpRequestActionBuilder = http("LoginPage")
    .get("${pageUrl}/login")
    .check(status().`in`(200, 304))

