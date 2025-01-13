package wellness.shop.Components.TheadTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FoodListManager {

    private List<String> foodItemList;

    public FoodListManager(List<String> foodItemList) {
        this.foodItemList = new ArrayList<>(foodItemList);;
    }

    /**
     * Gives one value from a list at a time. If no values available returns null
     *
     */
    public synchronized String getFoodItem(){
        if(foodItemList.isEmpty()) return null;
        String foodItem = foodItemList.getFirst();
        foodItemList.removeFirst();
        return foodItem;
    }

    public synchronized boolean isEmpty(){
        return foodItemList.isEmpty();
    }



}
