import { getParameter } from "@gatling.io/core";

// Define the target environment (default: DEV)
// Reference: https://docs.gatling.io/guides/passing-parameters/#javascript
const targetEnvironment = getParameter("env", "DEV");

// Resolve environment-specific configuration based on the target environment
const resolveUrls = (targetEnvironment: String) => {
  switch (targetEnvironment) {
    case "DEV":
      return {
        urls: {
          api: "https://api-ecomm.sandbox.gatling.io",
          web: "https://ecomm.sandbox.gatling.io"
        }
      };
    default:
      return {
        urls: {
          api: "https://ecomm.gatling.io",
          web: "https://api-ecomm.gatling.io"
        }
      };
  }
};

export default {
  ...resolveUrls(targetEnvironment),
  bodies: {
    cart: "bodies/cart.json"
  },
  feeders: {
    users: "data/users.json",
    products: "data/products.csv"
  },
  parameters: {
    vu: parseInt(getParameter("vu", "1")),
    duration: parseInt(getParameter("durationMinutes", "1")),
    rampDuration: parseInt(getParameter("rampDurationMinutes", "1")),
    minPauseSec: parseInt(getParameter("minPauseSec", "5")),
    maxPauseSec: parseInt(getParameter("maxPauseSec", "15")),
    testType: getParameter("testType", "smoke")
  }
};
