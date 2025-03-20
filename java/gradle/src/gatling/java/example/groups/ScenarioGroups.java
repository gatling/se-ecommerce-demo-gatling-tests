package example.groups;

import static example.endpoints.APIendpoints.*;
import static example.endpoints.WebEndpoints.*;
import static example.utils.Config.*;
import static example.utils.Keys.*;
import static io.gatling.javaapi.core.CoreDsl.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.*;
import java.util.ArrayList;
import java.util.List;

public class ScenarioGroups {

  // Define a record class for product details
  // Reference: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Record.html
  private record Product(
      int id,
      String name,
      String color,
      String price,
      int quantity,
      String imageSrc,
      String imageAlt) {}

  // ObjectMapper for JSON serialization/deserialization
  // Reference: https://fasterxml.github.io/jackson-databind/
  private static final ObjectMapper mapper = new ObjectMapper();

  // Define a feeder for user data
  // Reference: https://docs.gatling.io/reference/script/core/feeder/
  private static final FeederBuilder<Object> usersFeeder = jsonFile(usersFeederFile).circular();

  private static final FeederBuilder<String> productsFeeder = csv(productsFeederFile).circular();

  // Define home page actions for anonymous users
  // Reference: https://docs.gatling.io/reference/script/core/group/
  public static final ChainBuilder homeAnonymous =
      group("homeAnonymous")
          .on(homePage, session, exec(session -> session.set(PAGE_INDEX, 0)), products);

  // Define authentication process
  public static final ChainBuilder authenticate =
      group("authenticate")
          .on(loginPage, feed(usersFeeder), pause(minPauseSec, maxPauseSec), login);

  // Define home page actions for authenticated users
  public static final ChainBuilder homeAuthenticated =
      group("homeAuthenticated")
          .on(homePage, products, pause(minPauseSec, maxPauseSec), feed(productsFeeder), search);

  // Define adding a product to the cart
  // Reference: https://fasterxml.github.io/jackson-databind/javadoc/2.15/
  public static final ChainBuilder addToCart =
      group("addToCart")
          .on(
              exec(
                  session -> {
                    try {
                      // Deserialize product list from session
                      List<Product> products =
                          mapper.readValue(
                              session.getString(PRODUCTS), new TypeReference<List<Product>>() {});

                      // Select the first product and add it to cart
                      Product myFirstProduct = products.get(0);
                      List<Product> cartItems = new ArrayList<>();
                      cartItems.add(myFirstProduct);

                      // Serialize updated cart list back to session
                      String cartItemsJsonString = mapper.writeValueAsString(cartItems);
                      return session.set(CART_ITEMS, cartItemsJsonString);
                    } catch (Exception e) {
                      throw new RuntimeException(e);
                    }
                  }),
              cart);

  // Define checkout process
  public static final ChainBuilder buy = group("buy").on(checkOut);
}
