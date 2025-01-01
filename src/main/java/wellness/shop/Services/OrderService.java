package wellness.shop.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellness.shop.Models.BillingOrder.BillingOrder;
import wellness.shop.Models.BillingOrder.PaymentStatus;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Repositories.OrderRepository;

import java.util.List;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;


    public int registerOrder(BillingOrder billingOrder){
        return orderRepository.registerOrder(billingOrder);
    }

    public String updateOrder(BillingOrder billingOrder, String authorizationHeader){
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_PRODUCTS)) {
            return null;
        }
       return orderRepository.updateOrder(billingOrder) ? "Order was modified": "Order not Found";
    }

    public BillingOrder getOrderByID(int id, String authorizationHeader){
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_PRODUCTS)) {
            return null;
        }
        return orderRepository.getOrderByID(id);
    }

    public List<BillingOrder> getAllOrders(PaymentStatus paymentStatus, int limit, int offset, String authorizationHeader){
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_PRODUCTS)) {
            return null;
        }
        return orderRepository.getAllOrders(paymentStatus,limit,offset);
    }


    public String setPaymentStatus(int orderID, PaymentStatus paymentStatus, String authorizationHeader){
        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_PRODUCTS)) {
            return null;
        }
       return orderRepository.setPaymentStatus(orderID, paymentStatus) ? "Order paymentStatus was modified":"Order paymentStatus not found";
    }


    public BillingOrder getOrderByIDInternal(int id){
        return orderRepository.getOrderByID(id);
    }

    public String getProductJsonByOrderIDInternal(int id){
        return orderRepository.getProductJsonByOrderID(id);
    }

    public void setPaymentStatusInternal(int orderID, PaymentStatus paymentStatus){
        orderRepository.setPaymentStatus(orderID, paymentStatus);
    }

    public PaymentStatus getPaymentStatusInternal(int orderID){
        return orderRepository.getPaymentStatus(orderID);
    }

    public String getUsedPromoInternal(int orderID){
        return orderRepository.getPromoCode(orderID);
    }


}
