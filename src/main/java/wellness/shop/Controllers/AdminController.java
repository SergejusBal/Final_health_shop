package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Models.Users.Guest;
import wellness.shop.Security.JWT;
import wellness.shop.Services.UserService;
import wellness.shop.Utilities.UtilitiesGeneral;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JWT jwt;

    @Autowired
    private UserService userService;

    @DeleteMapping("/delete/user")
    public ResponseEntity<String> deleteUser(@RequestParam String username, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deleteUser(username, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PutMapping("/modify/role")
    public ResponseEntity<String> changeUserRole(@RequestParam String username, @RequestParam Role role, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.changeUserRole(username, role, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PutMapping("/add/privileges")
    public ResponseEntity<String> createPrivilege(@RequestParam String username, @RequestParam Privileges privileges, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.createPrivilege(username, privileges, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @DeleteMapping("/delete/privileges")
    public ResponseEntity<String> deletePrivilege(@RequestParam String username, @RequestParam Privileges privileges, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deletePrivilege(username, privileges, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PutMapping("/add/specialization")
    public ResponseEntity<String> createSpecialization(@RequestParam String username, @RequestParam Specialization specialization, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.createSpecialization(username, specialization, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @DeleteMapping("/delete/specialization")
    public ResponseEntity<String> deleteSpecialization(@RequestParam String username, @RequestParam Specialization specialization, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deleteSpecialization(username, specialization, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PutMapping("/add/subscription")
    public ResponseEntity<String> createSubscription(@RequestParam String username, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.createSubscription(username, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @PutMapping("/modify/subscription")
    public ResponseEntity<String> modifySubscription(@RequestParam String username, @RequestParam Boolean status, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.modifySubscription(username, status, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }

    @DeleteMapping("/delete/subscription")
    public ResponseEntity<String> deleteSubscription(@RequestParam String username,  @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deleteSubscription(username, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));

    }


}
