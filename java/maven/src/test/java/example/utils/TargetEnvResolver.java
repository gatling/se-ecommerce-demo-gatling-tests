package example.utils;

public class TargetEnvResolver {

  // Record to store environment-specific information
  public record EnvInfo(
      String pageUrl, String baseUrl, String usersFeederFile, String productsFeederFile) {}

  // Resolve environment-specific configuration based on the target environment
  public static EnvInfo resolveEnvironmentInfo(String targetEnv) {
    return switch (targetEnv) {
      case "DEV" ->
          new EnvInfo(
              "https://ecomm.gatling.io",
              "https://api-ecomm.gatling.io",
              "data/users_dev.json",
              "data/products_dev.csv");
      default ->
          new EnvInfo(
              "https://ecomm.gatling.io",
              "https://api-ecomm.gatling.io",
              "data/users_dev.json",
              "data/products_dev.csv");
    };
  }
}
