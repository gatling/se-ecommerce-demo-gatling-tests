package example.utils;

import java.time.Duration;

public class Config {

  // Define the target environment (default: DEV)
  // Reference:
  // https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
  public static final String targetEnv = System.getProperty("targetEnv", "DEV");

  // Load testing configuration
  public static final int vu = Integer.getInteger("vu", 1); // Number of virtual users
  public static final Duration duration =
      Duration.ofMinutes(Integer.getInteger("durationMinutes", 1)); // Test duration in minutes
  public static final Duration ramp_duration =
      Duration.ofMinutes(Integer.getInteger("rampDurationMinutes", 1)); // Ramp-up duration
  public static final int minPauseSec =
      Integer.getInteger("minPauseSec", 5); // Minimum pause between actions
  public static final int maxPauseSec =
      Integer.getInteger("maxPauseSec", 15); // Maximum pause between actions
  public static final String testType =
      System.getProperty("testType", "smoke"); // Test type (default: smoke)

  // Define percentage distribution for different regions
  public static final int frPerc = Integer.getInteger("frPerc", 60); // Percentage of French users
  public static final int usPerc = Integer.getInteger("usPerc", 40); // Percentage of US users

  // Resolve environment-specific configurations
  public static final TargetEnvResolver.EnvInfo envInfo =
      TargetEnvResolver.resolveEnvironmentInfo(targetEnv);
  public static final String pageUrl = envInfo.pageUrl(); // Base URL for web pages
  public static final String baseUrl = envInfo.baseUrl(); // Base API URL
  public static final String usersFeederFile =
      envInfo.usersFeederFile(); // Feeder file for users data
  public static final String productsFeederFile =
      envInfo.productsFeederFile(); // Feeder file for products data
}
