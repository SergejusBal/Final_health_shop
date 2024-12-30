package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Product;
import wellness.shop.Models.Users.Guest;
import wellness.shop.Services.ProductService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping("/public/{productID}")
    public ResponseEntity<Product> getProductById(@PathVariable int productID) {
        Product product = productService.getProductById(productID);

        if (product != null) return new ResponseEntity<>(product, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/public/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category, @RequestParam int limit, @RequestParam int offset) {

        List<Product> products = productService.getProductsByCategory(category, limit, offset);
        if (!products.isEmpty()) return new ResponseEntity<>(products, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping({"/public/name/","/public/name/{productName}"})
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable(required = false) String productName, @RequestParam int limit, @RequestParam int offset) {

        if(productName == null) productName = "";

        List<Product> products = productService.getProductsByName(productName, limit, offset);
        if (!products.isEmpty()) return new ResponseEntity<>(products, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/public/id/name/{productName}")
    public ResponseEntity<Integer> getProductIdByName(@PathVariable String productName) {
        int productId = productService.getProductIdByName(productName);

        if (productId != 0) return new ResponseEntity<>(productId, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/secured/new")
    public ResponseEntity<String> registerUser(@RequestBody Product product, @RequestHeader("Authorization") String authorizationHeader) {

        String response = productService.registerProduct(product,authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PutMapping("/secured/update/{productID}")
    public ResponseEntity<String> updateProductById(@RequestBody Product product, @PathVariable int productID, @RequestHeader("Authorization") String authorizationHeader) {

        String response = productService.updateProductById(product,productID,authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @DeleteMapping("/secured/delete/{nameOrProductID}")
    public ResponseEntity<String> deleteProduct( @PathVariable String nameOrProductID, @RequestHeader("Authorization") String authorizationHeader) {

        String response;

        try{
            response = productService.deleteProduct(authorizationHeader, Integer.parseInt(nameOrProductID));
        } catch (NumberFormatException e){
            response = productService.deleteProduct(authorizationHeader, nameOrProductID);
        }

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }



}