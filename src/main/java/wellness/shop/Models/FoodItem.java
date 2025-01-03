package wellness.shop.Models;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private int id;
    private String food;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Double fibers;

    public FoodItem(){
    }

    public int getId() {
        return id;
    }

    public String getFood() {
        return food;
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

    public Double getFibers() {
        return fibers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFood(String food) {
        this.food = food;
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

    public void setFibers(Double fibers) {
        this.fibers = fibers;
    }
}
