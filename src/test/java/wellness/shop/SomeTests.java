package wellness.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testng.Assert;
import wellness.shop.Models.BillingOrder.CartItem;
import wellness.shop.Models.Product;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Repositories.ProductRepository;
import wellness.shop.Repositories.UserRepository;
import wellness.shop.Security.JWT;
import wellness.shop.Services.OrderService;
import wellness.shop.Services.ProductService;
import wellness.shop.Services.StripeService;
import wellness.shop.Services.UserService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "jwt.key=6qfC9FoPtjiUgwcQClIIVn1izimFMfZWKLFoEC6tnEg=",
        "jwt.token.maxsize=1024",
        "jwt.token.expiration=1"
})
public class SomeTests {
    @Autowired
    private JWT jwt;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;

    @InjectMocks
    private UserService inejectedUserService;
    @InjectMocks
    private ProductService injectedProductService;
    @InjectMocks
    private StripeService injectedStripeService;

    @Test
    public void testJWTokenRoleAndUUIDCheck(){

        // Arrange
        String expectedUserUUID = UtilitiesGeneral.generateUID().toString();
        Role expectedRole = Role.ADMIN;
        String jwToken = jwt.generateJwt(expectedUserUUID,expectedRole);

        // Act
        String actualUserUUID = jwt.getUUID("Bearer " + jwToken);
        Role actualRole = jwt.getRole("Bearer " + jwToken);

        // Assert
        Assert.assertEquals(actualUserUUID,expectedUserUUID);
        Assert.assertEquals(actualRole,expectedRole);

    }


    @Test
    public void testGetUserNameByUUID(){

        // Arrange
        String expectedResponse = "admin";
        String userUUID = "00000000-0000-0000-0000-000000000000";
        Mockito.when(userRepository.getUsernameByUuid(userUUID)).thenReturn(expectedResponse);

        // Act
        String actualResponse = inejectedUserService.getUserNameByUUID(userUUID);

        // Assert
        Assert.assertEquals(actualResponse,expectedResponse);
    }

    @Test
    public void testGetProductByName(){

        // Arrange
        Product expectedProduct = new Product();
        expectedProduct.setId(1);
        expectedProduct.setName("Food item");
        expectedProduct.setCategory("Food category");
        expectedProduct.setDescription("This description");
        expectedProduct.setPrice(BigDecimal.valueOf(99.99));

        Mockito.when(productRepository.getProductById(1)).thenReturn(expectedProduct);
        // Act
        Product actualProduct = injectedProductService.getProductById(1);

        // Assert
        Assert.assertEquals(actualProduct.getName(),expectedProduct.getName());
        Assert.assertEquals(actualProduct.getCategory(),expectedProduct.getCategory());
        Assert.assertEquals(actualProduct.getDescription(),expectedProduct.getDescription());
        Assert.assertEquals(actualProduct.getPrice(), expectedProduct.getPrice());

    }


    @Test
    public void testCalculateOrderPriceAndGetOrderItems() throws Exception {

        // Arrange
        int orderId = 123;
        String jsonCartItems = "[{\"name\": \"Lactose-Free Milk\", \"price\": 3.99, \"category\": \"Dairy\", \"quantity\": 2, \"productId\": 66}," +
                "{\"name\": \"Coconut Yogurt\", \"price\": 4.99, \"category\": \"Dairy\", \"quantity\": 2, \"productId\": 56}]";

        // Mock
        Mockito.when(orderService.getProductJsonByOrderIDInternal(orderId)).thenReturn(jsonCartItems);

        // Mock
        Mockito.when(productService.getPriceById(66)).thenReturn(BigDecimal.valueOf(3.99));
        Mockito.when(productService.getPriceById(56)).thenReturn(BigDecimal.valueOf(4.99));

        // Act
        List<CartItem> cartItems = injectedStripeService.getOrderItemArrayByOrderID(orderId);
        Long totalOrderPrice = injectedStripeService.calculateOrderPrice(orderId);

        // Assert
        Assert.assertEquals(2, cartItems.size());
        Assert.assertEquals("Lactose-Free Milk", cartItems.get(0).getName());
        Assert.assertEquals("Coconut Yogurt", cartItems.get(1).getName());

        BigDecimal expectedTotal = BigDecimal.valueOf(3.99 * 2 + 4.99 * 2).multiply(BigDecimal.valueOf(100));
        Assert.assertEquals(expectedTotal.longValue(), totalOrderPrice);
    }


}
