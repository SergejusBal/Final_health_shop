package wellness.shop.Controllers;

import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Services.StripeService;
import wellness.shop.Utilities.UtilitiesGeneral;


@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/stripe")
public class StripeController {



    @Autowired
    private StripeService stripeService;

    @PostMapping("/pay/{orderID}")
    public ResponseEntity<String> createCheckoutSession(@PathVariable int orderID, @RequestHeader("Authorization") String authorizationHeader) {

            Session session = stripeService.createCheckoutSession(orderID,authorizationHeader);

            if(session != null) return new ResponseEntity<>(session.getId(), HttpStatus.OK);
            return new ResponseEntity<>("Order not found or stripe down", HttpStatus.NOT_FOUND);

    }

    @GetMapping("/refund/key/{refundKey}")
    public ResponseEntity<String>refund(@PathVariable String refundKey)  {

        String response = stripeService.refund(refundKey);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }





}
