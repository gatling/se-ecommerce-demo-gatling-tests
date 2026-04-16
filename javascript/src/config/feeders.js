import { csv, jsonFile } from "@gatling.io/core";

import config from ".";

// Define a feeder for user data
// Reference: https://docs.gatling.io/reference/script/core/feeder/
export const users = jsonFile(config.feeders.users).circular();
export const products = csv(config.feeders.products).circular();
