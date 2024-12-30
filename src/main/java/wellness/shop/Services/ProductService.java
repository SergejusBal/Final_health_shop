package wellness.shop.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellness.shop.Models.Product;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Repositories.ProductRepository;
import wellness.shop.Security.JWT;

import java.math.BigDecimal;
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
                    return productRepository.deleteProductByName(productName);
                },
                "Product was deleted",
                "Product not found"
        );
    }

    private String handleUserAction(Supplier<Boolean> action, String successMessage, String failureMessage) {
        return action.get() ? successMessage : failureMessage;
    }



}
