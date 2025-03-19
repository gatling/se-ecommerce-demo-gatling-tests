package example.groups

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.util.ArrayList
import java.util.List
import example.utils.Config._
import example.utils.Keys._
import example.endpoints.APIendpoints._
import example.endpoints.WebEndpoints._

object ScenarioGroups {

  // Define a record class for product details
  // Reference: https://docs.scala-lang.org/fr/tour/case-classes.html
  case class Product(id: Int, name: String, color: String, price: Double, quantity: Int, image: String, description: String, imageSrc: String, imageAlt: String)

  // ObjectMapper for JSON serialization/deserialization
  // Reference: https://fasterxml.github.io/jackson-databind/
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  // Define a feeder for user data
  // Reference: https://docs.gatling.io/reference/script/core/feeder/
  private val usersFeeder = jsonFile(usersFeederFile).circular

  private val productsFeeder = csv(productsFeederFile).circular

  // Define home page actions for anonymous users
  // Reference: https://docs.gatling.io/reference/script/core/group/
  val homeAnonymous: ChainBuilder = 
    group("homeAnonymous") (
      homePage,
      session,
      exec(session => session.set(PAGE_INDEX, 0)),
      products
    )

  // Define authentication process
  val authenticate: ChainBuilder = 
    group("authenticate") (
      loginPage,
      feed(usersFeeder),
      pause(1, 2),
      login
    )

  // Define home page actions for authenticated users
  val homeAuthenticated: ChainBuilder = 
    group("homeAuthenticated") (
      homePage,
      products,
      pause(1, 2),
      feed(productsFeeder),
      search
    )

  // Define adding a product to the cart
  // Reference: https://fasterxml.github.io/jackson-databind/javadoc/2.15/
  val addToCart: ChainBuilder = 
  group("addToCart") (
    exec { session =>
      try {
        // Deserialize product list from session
        val products: List[Product] = mapper.readValue(session(PRODUCTS).as[String], new TypeReference[List[Product]](){})
        val myFirstProduct: Product = products.get(0)

        // Select the first product and add it to cart
        val cartItems: List[Product] = new ArrayList[Product]
        cartItems.add(myFirstProduct)

        // Serialize updated cart list back to session
        val cartItemsJsonString = mapper.writeValueAsString(CART_ITEMS)
        session.set(CART_ITEMS, cartItemsJsonString)
      } catch {
        case e: Exception => throw new RuntimeException(e)
      }
    },
    cart
  )

  // Define checkout process
  val buy: ChainBuilder = 
    group("buy") (
      checkOut
    )
}
