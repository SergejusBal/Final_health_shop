package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Guest;
import wellness.shop.Models.Users.RegularUser;
import wellness.shop.Security.JWT;
import wellness.shop.Services.UserService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.util.HashMap;

@RestController
@RequestMapping("/guest")
public class GuestController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Guest user) {

        String response = userService.registerUser(user);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Guest user) {

        HashMap<String,String> response = userService.login(user);

        return new ResponseEntity<>(response.get("JWToken"), UtilitiesGeneral.checkHttpStatus(response.get("status")));

    }




}
