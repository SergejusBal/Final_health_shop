package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import wellness.shop.Models.Product;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Repositories.DateRegistrationRepository;
import wellness.shop.Repositories.ProductRepository;
import wellness.shop.Security.JWT;
import wellness.shop.Services.StripeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/noFilter")
public class NoFilterController {

    @Autowired
    private JWT jwt;

    @Value("${website.url}")
    private String frontEndURL;

    @Autowired
    private StripeService stripeService;

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Authorization") String authorizationHeader) {

        if(authorizationHeader == null || authorizationHeader.isEmpty()) {
            return new ResponseEntity<>(jwt.generateJwt("Guest", Role.GUEST), HttpStatus.OK);
        }

        Role role = jwt.getRole(authorizationHeader);
        String userUUID = jwt.getUUID(authorizationHeader);

        if(role != null && userUUID != null) {
            return new ResponseEntity<>(jwt.generateJwt(userUUID, role), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(jwt.generateJwt("Guest", Role.GUEST), HttpStatus.OK);
        }
    }

    @GetMapping("/stripe/{userUUID}/{secretUUID}/{paymentID}")
    public RedirectView redirect(@PathVariable String userUUID, @PathVariable String secretUUID, @PathVariable int paymentID) {
        stripeService.setProcessPaymentStatus(paymentID,userUUID,secretUUID);
        return new RedirectView(frontEndURL +"/success.html");
    }
    @GetMapping("/stripe/{userUUID}/{paymentID}")
    public RedirectView redirect(@PathVariable String userUUID,@PathVariable int paymentID) {
        stripeService.setProcessPaymentStatus(paymentID,userUUID,null);
        return new RedirectView(frontEndURL + "/fail.html");
    }


//    @Autowired
//    private DateRegistrationRepository dateRegistrationRepository;

//    @GetMapping("/test/{startDate}/{endDate}/{webKey}")
//    public ResponseEntity<List<LocalDateTime>> getTest(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate, @PathVariable String webKey) {
//        List<LocalDateTime> localDateTimeList = dateRegistrationRepository.getTimeSlotsWithinDateRange(startDate,endDate,webKey);
//        return new ResponseEntity(localDateTimeList,HttpStatus.OK);
//    }



}
