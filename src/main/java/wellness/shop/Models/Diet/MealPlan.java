package wellness.shop.Models.Diet;

import java.util.List;

public class MealPlan {
    private int day;
    private List<Meal> meals;
    private NutritionDetails nutritionDetails;

    public MealPlan() {
    }

    public int getDay() {
        return day;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public NutritionDetails getNutritionDetails() {
        return nutritionDetails;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public void setNutritionDetails(NutritionDetails nutritionDetails) {
        this.nutritionDetails = nutritionDetails;
    }
}
