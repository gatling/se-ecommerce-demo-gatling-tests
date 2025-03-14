package example.utils

// Data class to store environment-specific information
data class EnvInfo(
    val pageUrl: String,
    val baseUrl: String,
    val usersFeederFile: String,
    val productsFeederFile: String
)

object TargetEnvResolver {

    // Resolve environment-specific configuration based on the target environment
    fun resolveEnvironmentInfo(targetEnv: String): EnvInfo {
        return when (targetEnv) {
            "DEV" -> EnvInfo(
                pageUrl = "https://ecomm.gatling.io",
                baseUrl = "https://api-ecomm.gatling.io",
                usersFeederFile = "data/users_dev.json",
                productsFeederFile = "data/products_dev.csv"
            )
            else -> EnvInfo(
                pageUrl = "https://ecomm.gatling.io",
                baseUrl = "https://api-ecomm.gatling.io",
                usersFeederFile = "data/users_dev.json",
                productsFeederFile = "data/products_dev.csv"
            )
        }
    }
}
