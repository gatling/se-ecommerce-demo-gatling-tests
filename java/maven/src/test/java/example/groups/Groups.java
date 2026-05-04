package example.groups;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.config.Config;
import example.config.Feeders;
import example.requests.Api;
import example.requests.Web;
import io.gatling.javaapi.core.ChainBuilder;

public class Groups {

  // Define home page actions for anonymous users
  // Reference: https://docs.gatling.io/reference/script/core/group/
  public static final ChainBuilder homeAnonymous =
      group("Home anonymous")
          .on(Web.home, Api.session, exec(session -> session.set("pageIndex", 0)), Api.products);

  // Define authentication process
  public static final ChainBuilder authenticate =
      group("Authenticate")
          .on(
              Web.login,
              feed(Feeders.users),
              pause(Config.parameters.minPauseSec(), Config.parameters.maxPauseSec()),
              Api.login);

  // Define home page actions for authenticated users
  public static final ChainBuilder homeAuthenticated =
      group("Home authenticated")
          .on(
              Web.home,
              Api.products,
              pause(Config.parameters.minPauseSec(), Config.parameters.maxPauseSec()),
              feed(Feeders.products),
              Api.search);

  // Define adding a product to the cart
  public static final ChainBuilder cart = group("Cart").on(Api.addToCart);

  // Define checkout process
  public static final ChainBuilder buy = group("Buy").on(Api.checkout);
}
