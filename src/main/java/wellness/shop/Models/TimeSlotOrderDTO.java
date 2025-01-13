package wellness.shop.Models;

import java.time.LocalDateTime;

public class TimeSlotOrderDTO {
    private LocalDateTime timeSlot;
    private String employeeUuid;
    private String userUUID;
    private int orderId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private int productId;
    private String paymentStatus;

    public TimeSlotOrderDTO() {
    }

    public LocalDateTime getTimeSlot() {
        return timeSlot;
    }

    public String getEmployeeUuid() {
        return employeeUuid;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getProductId() {
        return productId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setTimeSlot(LocalDateTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public void setEmployeeUuid(String employeeUuid) {
        this.employeeUuid = employeeUuid;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getUserUUID() {
        return userUUID;
    }
}
