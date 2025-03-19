package example.endpoints

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import example.utils.Config._

object WebEndpoints {

  // Define the home page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  val homePage = http("HomePage")
    .get(pageUrl)
    .check(status.in(200, 304)) // Accept both OK (200) and Not Modified (304) statuses

  // Define the login page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  val loginPage = http("LoginPage")
    .get(s"$pageUrl/login")
    .check(status.in(200, 304))
}
