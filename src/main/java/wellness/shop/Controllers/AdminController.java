package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Models.Users.Guest;
import wellness.shop.Models.Users.User;
import wellness.shop.Security.JWT;
import wellness.shop.Services.UserService;
import wellness.shop.Utilities.UtilitiesGeneral;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JWT jwt;

    @Autowired
    private UserService userService;


    @GetMapping("/get/user/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username, @RequestHeader("Authorization") String authorizationHeader) {

        User user = userService.getUserInfo(username, authorizationHeader);

        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (user.getID() == 0) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else{
            user.setPassword(null);
            return new ResponseEntity<>(user ,HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deleteUser(username, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/modify/role/{username}/{role}")
    public ResponseEntity<String> changeUserRole(@PathVariable String username, @PathVariable Role role, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.changeUserRole(username, role, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/add/privileges/{username}/{privileges}")
    public ResponseEntity<String> createPrivilege(@PathVariable String username, @PathVariable Privileges privileges, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.createPrivilege(username, privileges, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @DeleteMapping("/delete/privileges/{username}/{privileges}")
    public ResponseEntity<String> deletePrivilege(@PathVariable String username, @PathVariable Privileges privileges, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deletePrivilege(username, privileges, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/add/specialization/{username}/{specialization}")
    public ResponseEntity<String> createSpecialization(@PathVariable String username, @PathVariable Specialization specialization, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.createSpecialization(username, specialization, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @DeleteMapping("/delete/specialization/{username}/{specialization}")
    public ResponseEntity<String> deleteSpecialization(@PathVariable String username, @PathVariable Specialization specialization, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deleteSpecialization(username, specialization, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/add/subscription/{username}")
    public ResponseEntity<String> createSubscription(@PathVariable String username, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.createSubscription(username, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/modify/subscription/{username}/{status}")
    public ResponseEntity<String> modifySubscription(@PathVariable String username, @PathVariable Boolean status, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.modifySubscription(username, status, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @DeleteMapping("/delete/subscription/{username}")
    public ResponseEntity<String> deleteSubscription(@PathVariable String username, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.deleteSubscription(username, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }



}
