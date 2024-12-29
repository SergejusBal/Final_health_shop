package wellness.shop.Models.Diet;

public class Ingredient {
    private String name;
    private Double quantity;

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
