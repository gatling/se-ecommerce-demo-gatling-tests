package example.utils

object TargetEnvResolver {

  // Record to store environment-specific information
  case class EnvInfo(pageUrl: String, baseUrl: String, usersFeederFile: String, productsFeederFile: String)

  // Resolve environment-specific configuration based on the target environment
  def resolveEnvironmentInfo(targetEnv: String): EnvInfo = targetEnv match {
    case "DEV" =>
      EnvInfo("https://ecomm.gatling.io", "https://api-ecomm.gatling.io", "data/users_dev.json", "data/products_dev.csv")
    case _ =>
      EnvInfo("https://ecomm.gatling.io", "https://api-ecomm.gatling.io", "data/users_dev.json", "data/products_dev.csv")
  }
}
