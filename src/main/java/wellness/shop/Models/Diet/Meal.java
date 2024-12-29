package wellness.shop.Models.Diet;

import java.time.LocalDateTime;
import java.util.List;

public class Meal {
    private LocalDateTime time;
    private String type;
    private String dishName;
    private List<Ingredient> ingredients;
    private NutritionDetails nutritionDetails;
    private String preparationSteps;

    public Meal() {
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getDishName() {
        return dishName;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public NutritionDetails getNutritionDetails() {
        return nutritionDetails;
    }

    public String getPreparationSteps() {
        return preparationSteps;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setNutritionDetails(NutritionDetails nutritionDetails) {
        this.nutritionDetails = nutritionDetails;
    }

    public void setPreparationSteps(String preparationSteps) {
        this.preparationSteps = preparationSteps;
    }
}
