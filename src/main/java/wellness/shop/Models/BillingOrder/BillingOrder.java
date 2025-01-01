package wellness.shop.Models.BillingOrder;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingOrder {
    private int id;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String orderCart;
    private PaymentStatus paymentStatus;
    private String promoCode;
    private String customerPhone;
    private String userUUID;

    public BillingOrder() {
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getOrderCart() {
        return orderCart;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setOrderCart(String orderCart) {
        this.orderCart = orderCart;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }
}