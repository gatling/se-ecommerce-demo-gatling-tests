import { group, pause } from "@gatling.io/core";
import { session, login, search, cart, products, checkOut } from "../endpoints/apiEndpoints";
import { homePage, loginPage } from "../endpoints/webEndpoints";
import { maxPauseSec, minPauseSec } from "../utils/config";

// Define home page actions for anonymous users
// Reference: https://docs.gatling.io/reference/script/core/group/
export const homeAnonymous = group("homeAnonymous").on(homePage, session, products);

// Define authentication process
export const authenticate = group("authenticate").on(
  loginPage,
  pause(minPauseSec, maxPauseSec),
  login
);

// Define home page actions for authenticated users
export const homeAuthenticated = group("homeAuthenticated").on(
  homePage,
  products,
  pause(minPauseSec, maxPauseSec),
  search
);

// Define adding a product to the cart
export const addToCart = group("addToCart").on(cart);

// Define checkout process
export const buy = group("buy").on(checkOut);
