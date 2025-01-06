package wellness.shop.Services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wellness.shop.Integration.Redis;
import wellness.shop.Models.FoodItem;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Repositories.FoodRepository;

import java.io.IOException;
import java.util.function.Supplier;


@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatAIService chatAIService;

    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;

    private Redis redis;
    @PostConstruct
    public void init(){
        this.redis = new Redis(redisHost, redisPort);
    }


    public FoodItem getFoodItem(String foodName) {

        FoodItem foodItem;

        try {
            foodItem = (FoodItem) redis.getWithDeserialize("$&food&$" + foodName);
        } catch (IOException | ClassNotFoundException ignored) {
            foodItem = null;
        }

        if(foodItem != null) return foodItem;
        else foodItem = foodRepository.getFoodItemByFoodName(foodName);


        if(foodItem != null) {
            try {
                redis.putWithSerialize("$&food&$"+foodName,foodItem, 600);
            } catch (IOException ignore) {}

            return foodItem;
        }
        foodItem = chatAIService.getFoodItemFromAI(foodName);
        if(foodItem != null ) registerFoodItemIA(foodItem);
        return foodItem;
    }

    public void registerFoodItemIA(FoodItem foodItem){
        foodRepository.registerFoodItem(foodItem);
    }

    public String registerFoodItem(FoodItem foodItem, String authorizationHeader) {
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_FOOD_ITEMS)) {
            return "No authorization";
        }

        return handleUserAction(
                () -> {
                    return foodRepository.registerFoodItem(foodItem);
                },
                "FoodItem was successfully added",
                "FoodItem already exists"
        );
    }

    public String updateFoodItemByFoodName(FoodItem foodItem, String authorizationHeader) {
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_FOOD_ITEMS)) {
            return "No authorization";
        }

        try {
            redis.putWithSerialize("$&food&$"+foodItem.getFood(),foodItem, 600);
        } catch (IOException ignore) {}

        return handleUserAction(
                () -> {
                    return foodRepository.updateFoodItemByFoodName(foodItem);
                },
                "FoodItem was successfully modified",
                "Fail to add FoodItem or it already exists"
        );
    }


    public String deleteFoodItem(String foodName, String authorizationHeader) {
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_FOOD_ITEMS)) {
            return "No authorization";
        }

        redis.delete("$&food&$"+foodName);

        return handleUserAction(
                () -> {
                    return foodRepository.deleteFoodItem(foodName);
                },
                "FoodItem was successfully deleted",
                "FoodItem not found"
        );
    }


    private String handleUserAction(Supplier<Boolean> action, String successMessage, String failureMessage) {
        return action.get() ? successMessage : failureMessage;
    }




}
