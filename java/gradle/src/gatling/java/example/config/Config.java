package example.config;

import java.time.Duration;

public class Config {

  // Define the target environment (default: DEV)
  // Reference:
  // https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
  private static final String targetEnvironment = System.getProperty("env", "DEV");

  public record Urls(String api, String web) {}

  public record Bodies(String cart) {}

  public record Feeders(String users, String products) {}

  public record Parameters(
      int vu,
      Duration duration,
      Duration rampDuration,
      int minPauseSec,
      int maxPauseSec,
      String testType) {}

  // Resolve environment-specific configuration based on the target environment
  private static Urls resolveUrls(String targetEnvironment) {
    if ("DEV".equals(targetEnvironment)) {
      return new Urls("https://api-ecomm.sandbox.gatling.io", "https://ecomm.sandbox.gatling.io");
    }

    return new Urls("https://api-ecomm.gatling.io", "https://ecomm.gatling.io");
  }

  // Load testing configuration
  public static final Urls urls = resolveUrls(targetEnvironment);
  public static final Bodies bodies = new Bodies("bodies/cart.json");
  public static final Feeders feeders = new Feeders("data/users.json", "data/products.csv");
  public static final Parameters parameters =
      new Parameters(
          Integer.getInteger("vu", 1), // Number of virtual users
          Duration.ofMinutes(Integer.getInteger("durationMinutes", 1)), // Test
          Duration.ofMinutes(Integer.getInteger("rampDurationMinutes", 1)), // Ramp-up
          Integer.getInteger("minPauseSec", 5), // Minimum pause between actions
          Integer.getInteger("maxPauseSec", 15), // Maximum pause between actions
          System.getProperty("testType", "smoke") // Test type (default: smoke)
          );
}
