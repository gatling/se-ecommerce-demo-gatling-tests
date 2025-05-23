import { getParameter } from "@gatling.io/core";
import { postman } from "@gatling.io/postman";

// Define the postman collection with its corresponding environment
// Reference: https://docs.gatling.io/reference/script/protocols/postman/#import-collections
export const collection = postman
  .fromResource("gatlingEcommerce.postman_collection.json")
  .environment("gatlingEcommerce.postman_environment.json");

// Define the target environment (default: DEV)
// Reference: https://docs.gatling.io/guides/passing-parameters/#javascript
export const targetEnv = getParameter("targetEnv", "DEV");

// Load testing configuration
export const vu = parseInt(getParameter("vu", "1"));
export const duration = parseInt(getParameter("durationMinutes", 1));
export const ramp_duration = parseInt(getParameter("rampDurationMinutes", 1));
export const minPauseSec = parseInt(getParameter("minPauseSec", "5"));
export const maxPauseSec = parseInt(getParameter("maxPauseSec", "15"));
export const testType = getParameter("testType", "stress");

// Define percentage distribution for different regions
export const frPerc = parseFloat(getParameter("frPerc", "60.0"));
export const usPerc = parseFloat(getParameter("usPerc", "40.0"));
