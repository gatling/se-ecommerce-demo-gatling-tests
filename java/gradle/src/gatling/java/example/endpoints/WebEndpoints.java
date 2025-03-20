package example.endpoints;

import static example.utils.Config.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

public class WebEndpoints {

  // Define the home page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  public static final HttpRequestActionBuilder homePage =
      http("HomePage")
          .get(pageUrl)
          .check(status().in(200, 304)); // Accept both OK (200) and Not Modified (304) statuses

  // Define the login page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  public static final HttpRequestActionBuilder loginPage =
      http("LoginPage").get(pageUrl + "/login").check(status().in(200, 304));
}
