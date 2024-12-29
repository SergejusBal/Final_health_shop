package wellness.shop.Models.Diet;

public class NutritionDetails {

    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Double fiber;

    public NutritionDetails() {
    }

    public Double getCalories() {
        return calories;
    }

    public Double getProteins() {
        return proteins;
    }

    public Double getFats() {
        return fats;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public Double getFiber() {
        return fiber;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public void setProteins(Double proteins) {
        this.proteins = proteins;
    }

    public void setFats(Double fats) {
        this.fats = fats;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public void setFiber(Double fiber) {
        this.fiber = fiber;
    }
}
