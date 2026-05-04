package example.requests;

import static io.gatling.javaapi.http.HttpDsl.*;

import example.config.Config;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

public class Web {

  // Define the home page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  public static final HttpRequestActionBuilder home =
      http("Home page")
          .get(Config.urls.web())
          // Accept both OK (200) and Not Modified (304) statuses
          .check(status().in(200, 304));

  // Define the login page request with response status validation
  // Reference: https://docs.gatling.io/reference/script/protocols/http/request/#checks
  public static final HttpRequestActionBuilder login =
      http("Login page").get(Config.urls.web() + "/login").check(status().in(200, 304));
}
