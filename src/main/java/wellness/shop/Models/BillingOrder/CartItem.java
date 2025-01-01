package wellness.shop.Models.BillingOrder;

public class CartItem {
    private int productId;
    private String category;
    private String name;
    private int quantity;

    public CartItem() {
    }


    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
