package example.groups

import example.endpoints.*
import example.utils.*

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.ArrayList

data class Product(
    val id: Int,
    val name: String,
    val color: String,
    val price: String,
    val quantity: Int,
    val imageSrc: String,
    val imageAlt: String
)

private val mapper: ObjectMapper = jacksonObjectMapper()

// Define a feeder for user data
// Reference: https://docs.gatling.io/reference/script/core/feeder/
private val usersFeeder = jsonFile(usersFeederFile).circular()

private val productsFeeder = csv(productsFeederFile).circular()

// Define home page actions for anonymous users
// Reference: https://docs.gatling.io/reference/script/core/group/
val homeAnonymous: ChainBuilder = group("homeAnonymous")
    .on(homePage, session, exec { session -> session.set(PAGE_INDEX, 0) }, products)

// Define authentication process
val authenticate: ChainBuilder = group("authenticate")
    .on(loginPage, feed(usersFeeder), pause(minPauseSec, maxPauseSec), login)

// Define home page actions for authenticated users
val homeAuthenticated: ChainBuilder = group("homeAuthenticated")
    .on(homePage, products, pause(minPauseSec, maxPauseSec), feed(productsFeeder), search)

// Define adding a product to the cart
val addToCart: ChainBuilder = group("addToCart")
    .on(
        exec { session ->
            try {
                // Deserialize product list from session
                val products: List<Product> = mapper.readValue(
                    session.getString(PRODUCTS), object : TypeReference<List<Product>>() {}
                )

                // Select the first product and add it to cart
                val myFirstProduct = products[0]
                val cartItems = ArrayList<Product>()
                cartItems.add(myFirstProduct)

                // Serialize updated cart list back to session
                val cartItemsJsonString = mapper.writeValueAsString(cartItems)
                session.set(CART_ITEMS, cartItemsJsonString)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        },
        cart
    )

// Define checkout process
val buy: ChainBuilder = group("buy").on(checkOut)

