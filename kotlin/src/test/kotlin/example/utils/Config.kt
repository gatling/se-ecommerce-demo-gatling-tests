package example.utils

import java.time.Duration

object Config {

    // Define the target environment (default: DEV)
    // Reference:
    // https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
    val targetEnv: String = System.getProperty("targetEnv", "DEV")

    // Load testing configuration
    val vu: Int = System.getProperty("vu")?.toInt() ?: 1 // Number of virtual users
    val duration: Duration = Duration.ofMinutes(System.getProperty("durationMinutes")?.toInt()?.toLong() ?: 1L) // Test duration in minutes
    val rampDuration: Duration = Duration.ofMinutes(System.getProperty("rampDurationMinutes")?.toInt()?.toLong() ?: 1L) // Ramp-up duration
    val minPauseSec: Long = System.getProperty("minPauseSec")?.toInt()?.toLong() ?: 5 // Minimum pause between actions
    val maxPauseSec: Long = System.getProperty("maxPauseSec")?.toInt()?.toLong() ?: 15 // Maximum pause between actions
    val testType: String = System.getProperty("testType", "smoke") // Test type (default: smoke)

    // Define percentage distribution for different regions
    val frPerc: Double = System.getProperty("frPerc")?.toDouble() ?: 60.0 // Percentage of French users
    val usPerc: Double = System.getProperty("usPerc")?.toDouble() ?: 40.0 // Percentage of US users

    // Resolve environment-specific configurations
    val envInfo: EnvInfo = TargetEnvResolver.resolveEnvironmentInfo(targetEnv)
    val pageUrl: String = envInfo.pageUrl // Base URL for web pages
    val baseUrl: String = envInfo.baseUrl // Base API URL
    val usersFeederFile: String = envInfo.usersFeederFile // Feeder file for users data
    val productsFeederFile: String = envInfo.productsFeederFile // Feeder file for products data
}
