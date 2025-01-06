package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.BillingOrder.PaymentStatus;
import wellness.shop.Models.FoodItem;
import wellness.shop.Models.TimeSlotOrderDTO;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Security.JWT;
import wellness.shop.Services.EmployeeService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private JWT jwt;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/get/{paymentStatus}/{day}")
    public ResponseEntity<List<TimeSlotOrderDTO>> getServiceOrdersByEmployeeAndDay(@PathVariable String paymentStatus, @PathVariable LocalDate day, @RequestHeader("Authorization") String authorizationHeader) {

        List<TimeSlotOrderDTO> timeSlotList = employeeService.getOrdersByEmployeeAndDay(paymentStatus,day,authorizationHeader);

        if(timeSlotList == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (timeSlotList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(timeSlotList, HttpStatus.OK);

    }

    @GetMapping("/set/{paymentStatus}/order/{orderID}")
    public ResponseEntity<String> setPaymentStatus(@PathVariable PaymentStatus paymentStatus, @PathVariable int orderID, @RequestHeader("Authorization") String authorizationHeader) {

        String response = employeeService.getOrdersByEmployeeAndDay(paymentStatus,orderID,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }



}


