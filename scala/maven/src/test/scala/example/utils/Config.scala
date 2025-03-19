package example.utils

import scala.concurrent.duration._

object Config {

  // Define the target environment (default: DEV)
  // Reference:
  // https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
  val targetEnv: String = System.getProperty("targetEnv", "DEV")

  // Load testing configuration
  val vu: Int = Integer.getInteger("vu", 1)
  val duration: FiniteDuration =
    Duration(java.lang.Long.getLong("durationMinutes", 1), MINUTES)
  val rampDuration: FiniteDuration =
    Duration(java.lang.Long.getLong("rampDurationMinutes", 1), MINUTES)
  val minPauseSec: Integer = Integer.getInteger("minPauseSec", 5)
  val maxPauseSec: Integer = Integer.getInteger("maxPauseSec", 15)
  val testType: String = System.getProperty("type", "smoke")

  // Define percentage distribution for different regions
  val frPerc: Double =
    Option(System.getProperty("frPerc")).map(_.toDouble).getOrElse(60.0)
  val usPerc: Double =
    Option(System.getProperty("usPerc")).map(_.toDouble).getOrElse(40.0)

  // Resolve environment-specific configurations
  val envInfo = TargetEnvResolver.resolveEnvironmentInfo(targetEnv)
  val pageUrl: String = envInfo.pageUrl // Base URL for web pages
  val baseUrl: String = envInfo.baseUrl // Base API URL
  val usersFeederFile: String =
    envInfo.usersFeederFile // Feeder file for users data
  val productsFeederFile: String =
    envInfo.productsFeederFile // Feeder file for products data
}
