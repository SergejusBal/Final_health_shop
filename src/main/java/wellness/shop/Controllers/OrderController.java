package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.BillingOrder.BillingOrder;
import wellness.shop.Models.BillingOrder.PaymentStatus;
import wellness.shop.Models.FoodItem;
import wellness.shop.Services.OrderService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/public/new")
    public ResponseEntity<Integer> registerOrder(@RequestBody BillingOrder billingOrder){

        Integer orderID = orderService.registerOrder(billingOrder);

        if(orderID > 0) return new ResponseEntity<>(orderID, HttpStatus.OK);
        else if (orderID == 0) return new ResponseEntity<>(orderID, HttpStatus.CONFLICT);
        else return new ResponseEntity<>(orderID, HttpStatus.BAD_REQUEST);
    }


    @PutMapping("/secured/update")
    public ResponseEntity<String> updateOrder(@RequestBody BillingOrder billingOrder, @RequestHeader("Authorization") String authorizationHeader){

        String response = orderService.updateOrder(billingOrder,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @GetMapping("/secured/get/{orderID}")
    public ResponseEntity<BillingOrder> getOrderByID(@PathVariable Integer orderID, @RequestHeader("Authorization") String authorizationHeader){

        BillingOrder billingOrder = orderService.getOrderByID(orderID,authorizationHeader);

        if(billingOrder == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (billingOrder.getId() == 0) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(billingOrder, HttpStatus.OK);
    }


    @GetMapping("/secured/get/all/{paymentStatus}")
    public ResponseEntity<List<BillingOrder>> getOrderByID(@PathVariable PaymentStatus paymentStatus, @RequestParam int limit, @RequestParam int offset, @RequestHeader("Authorization") String authorizationHeader){

        List<BillingOrder> billingOrderList = orderService.getAllOrders(paymentStatus,limit,offset,authorizationHeader);

        if(billingOrderList == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (billingOrderList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(billingOrderList, HttpStatus.OK);
    }

    @GetMapping("/secured/set/{paymentStatus}/order/{orderID}")
    public ResponseEntity<String> setPaymentStatus(@PathVariable Integer orderID, @PathVariable PaymentStatus paymentStatus, @RequestHeader("Authorization") String authorizationHeader){

        String response = orderService.setPaymentStatus(orderID,paymentStatus,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }




}
