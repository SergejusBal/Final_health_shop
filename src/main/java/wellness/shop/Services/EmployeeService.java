package wellness.shop.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellness.shop.Models.BillingOrder.PaymentStatus;
import wellness.shop.Models.TimeSlotOrderDTO;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Repositories.EmployeeRepository;
import wellness.shop.Repositories.OrderRepository;
import wellness.shop.Security.JWT;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JWT jwt;

    public List<TimeSlotOrderDTO> getOrdersByEmployeeAndDay(String paymentStatus, LocalDate timeSlotDay, String authorizationHeader) {
        if (    !(userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN) ||
                userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS)))
        {
            return null;
        }
        String employeeUUID = jwt.getUUID(authorizationHeader);

        return employeeRepository.getOrdersByEmployeeAndDay(employeeUUID, paymentStatus, timeSlotDay);
    }


    public String getOrdersByEmployeeAndDay(PaymentStatus paymentStatus, int orderID, String authorizationHeader) {
        if (    !(userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN) ||
                userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS)))
        {
            return "No authorization";
        }

        return orderRepository.setPaymentDietServicePaymentStatus(orderID, paymentStatus) ? "Order paymentStatus was modified" : "Order paymentStatus not found";
    }




}
