import { group, jsonFile, feed, exec, pause, csv } from "@gatling.io/core";
import { session, login, search, cart, products, checkOut } from "../endpoints/apiEndpoints";
import { homePage, loginPage } from "../endpoints/webEndpoints";
import { maxPauseSec, minPauseSec, productsFeederFile } from "../utils/config";
import { CART_ITEMS, PAGE_INDEX, PRODUCTS } from "../utils/keys";

// Define a feeder for user data
// Reference: https://docs.gatling.io/reference/script/core/feeder/
export const usersFeeder = jsonFile("data/users_dev1.json").circular();

export const productsFeeder = csv(productsFeederFile).circular();

// Define home page actions for anonymous users
// Reference: https://docs.gatling.io/reference/script/core/group/
export const homeAnonymous = group("homeAnonymous").on(
  homePage,
  session,
  exec((session) => session.set(PAGE_INDEX, 0)),
  products
);

// Define authentication process
export const authenticate = group("authenticate").on(
  loginPage,
  feed(usersFeeder),
  pause(minPauseSec, maxPauseSec),
  login
);

// Define home page actions for authenticated users
export const homeAuthenticated = group("homeAuthenticated").on(
  homePage,
  products,
  pause(minPauseSec, maxPauseSec),
  feed(productsFeeder),
  search
);

// Define adding a product to the cart
export const addToCart = group("addToCart").on(
  exec((session) => {
    try {
      const products = JSON.parse(session.get(PRODUCTS));
      const myFirstProduct = products[0];

      const cartItems = [myFirstProduct];
      const cartItemsJsonString = JSON.stringify(cartItems);

      return session.set(CART_ITEMS, cartItemsJsonString);
    } catch (error) {
      console.error("An error occurred:", error);
      return session.set(CART_ITEMS, JSON.stringify([]));
    }
  }),
  cart
);

// Define checkout process
export const buy = group("buy").on(checkOut);
