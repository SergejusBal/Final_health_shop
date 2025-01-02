package wellness.shop.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellness.shop.Models.Product;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Repositories.DateRegistrationRepository;
import wellness.shop.Repositories.ProductRepository;
import wellness.shop.Security.JWT;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DateRegistrationRepository dateRegistrationRepository;

    @Autowired
    private JWT jwt;

    public Product getProductById(int productID){
        return productRepository.getProductById(productID);
    }
    public List<Product> getProductsByCategory(String category,int limit, int offset){
        return productRepository.getProductsByCategory(category,limit,offset);
    }
    public List<Product> getProductsByName(String productName,int limit, int offset){
        return productRepository.getProductsByName(productName,limit,offset);
    }

    public int getProductIdByName(String productName){
        return productRepository.getProductIdByName(productName);
    }

    public BigDecimal getPriceById(int productID){
        return productRepository.getPriceById(productID);
    }

    public String updateProductById(Product product, int productIDid,  String authorizationHeader) {
        if (!userService.isAuthorized(authorizationHeader,Privileges.MODIFY_PRODUCTS)) {
            return "No authorization";
        }

        return handleUserAction(
                () -> {
                    return productRepository.updateProductById(product,productIDid);
                },
                "Product was modified",
                "Product not found"
        );
    }

    public String registerProduct(Product product, String authorizationHeader) {
        if (!userService.isAuthorized(authorizationHeader,Privileges.MODIFY_PRODUCTS)) {
            return "No authorization";
        }

        return handleUserAction(
                () -> {
                    return productRepository.registerProduct(product);
                },
                "Product was added",
                "Fail to add product"
        );
    }

    public String registerProductService(Product product, String employeeName, String authorizationHeader) {
        if (!userService.isAuthorized(authorizationHeader,Privileges.MODIFY_PRODUCTS)) {
            return "No authorization";
        }

        return handleUserAction(
                () -> {
                    productRepository.registerProduct(product);
                    int productID = productRepository.getProductIdByName(product.getName());
                    String employeeUUID = userService.getUserUUIDByName(employeeName);
                    if(employeeUUID == null) {
                        productRepository.deleteProductByID(productID);
                        return false;
                    }
                    String websocketKey = UtilitiesGeneral.generateUID().toString();
                    if(productRepository.registerProductService(employeeUUID,productID, websocketKey))
                        return generateSchedule(websocketKey);

                    else return false;
                },
                "Product was added",
                "Fail to add product"
        );
    }


    public String deleteProduct(String authorizationHeader, int productID) {
        if (!userService.isAuthorized(authorizationHeader,Privileges.MODIFY_PRODUCTS)) {
            return "No authorization";
        }

        return handleUserAction(
                () -> {
                    return productRepository.deleteProductByID(productID);
                },
                "Product was deleted",
                "Product not found"
        );
    }

    public String deleteProduct(String authorizationHeader, String productName) {
        if (!userService.isAuthorized(authorizationHeader,Privileges.MODIFY_PRODUCTS)) {
            return "No authorization";
        }

        return handleUserAction(
                () -> {
                    int productID = productRepository.getProductIdByName(productName);
                    return productRepository.deleteProductByID(productID);
                },
                "Product was deleted",
                "Product not found"
        );
    }

    public boolean registerDietServiceInternal(int orderID, int productID) {
        return productRepository.registerDietService(orderID, productID);
    }

    private boolean generateSchedule(String websocketKey){

        for(int i = 1; i <= 29; i++){

            List<LocalDateTime> dateTimes = UtilitiesGeneral.generateFutureTimeSlots(i);
            if(dateTimes == null) continue;

            for(LocalDateTime localDateTime: dateTimes){
                dateRegistrationRepository.registerRegistrationDate(localDateTime,websocketKey);

            }
        }

        return true;
    }

    private String handleUserAction(Supplier<Boolean> action, String successMessage, String failureMessage) {
        return action.get() ? successMessage : failureMessage;
    }




}
