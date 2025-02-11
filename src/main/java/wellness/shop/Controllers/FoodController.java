package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Diet.Diet;
import wellness.shop.Models.FoodItem;
import wellness.shop.Services.FoodService;
import wellness.shop.Utilities.UtilitiesGeneral;

@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/user/get/{foodName}")
    public ResponseEntity<FoodItem> getFoodItem(@PathVariable String foodName){
       FoodItem foodItem = foodService.getFoodItem(foodName);
       if(foodItem != null) return new ResponseEntity<>(foodItem, HttpStatus.OK);
       else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/secured/new")
    public ResponseEntity<String> registerFoodItem(@RequestBody FoodItem foodItem, @RequestHeader("Authorization") String authorizationHeader){
        String response = foodService.registerFoodItem(foodItem,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/secured/update")
    public ResponseEntity<String> updateFoodItemByFoodName(@RequestBody FoodItem foodItem, @RequestHeader("Authorization") String authorizationHeader){
        String response = foodService.updateFoodItemByFoodName(foodItem,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @DeleteMapping("/secured/delete/{foodName}")
    public ResponseEntity<String> deleteFoodItem(@PathVariable String foodName, @RequestHeader("Authorization") String authorizationHeader) {

        String response = foodService.deleteFoodItem(foodName, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }


}
