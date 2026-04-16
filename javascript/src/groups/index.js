import { group, feed, exec, pause } from "@gatling.io/core";

import config from "../config";
import * as feeders from "../config/feeders";
import * as api from "../requests/api";
import * as web from "../requests/web";

// Define home page actions for anonymous users
// Reference: https://docs.gatling.io/reference/script/core/group/
export const homeAnonymous = group("Home anonymous").on(
  web.home,
  api.session,
  exec((session) => session.set("pageIndex", 0)),
  api.products
);

// Define authentication process
export const authenticate = group("Authenticate").on(
  web.login,
  feed(feeders.users),
  pause(config.parameters.minPauseSec, config.parameters.maxPauseSec),
  api.login
);

// Define home page actions for authenticated users
export const homeAuthenticated = group("Home authenticated").on(
  web.home,
  api.products,
  pause(config.parameters.minPauseSec, config.parameters.maxPauseSec),
  feed(feeders.products),
  api.search
);

// Define adding a product to the cart
export const cart = group("Cart").on(api.addToCart);

// Define checkout process
export const buy = group("Buy").on(api.checkout);
