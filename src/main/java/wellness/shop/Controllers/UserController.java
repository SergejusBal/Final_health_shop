package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Models.Users.Profile;
import wellness.shop.Security.JWT;
import wellness.shop.Services.UserService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JWT jwt;

    @Autowired
    private UserService userService;

    @PutMapping("/modify/profile")
    public ResponseEntity<String> changeUserRole(@RequestBody Profile profile, @RequestHeader("Authorization") String authorizationHeader) {

        String response = userService.updateProfile(profile, authorizationHeader);

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }


    @GetMapping("/get/profile/{userUUID}")
    public ResponseEntity<Profile> changeUserRole(@PathVariable String userUUID, @RequestHeader("Authorization") String authorizationHeader) {

        Profile profile = userService.getProfile(userUUID, authorizationHeader);

        if(profile == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (profile.getID() == 0) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(profile,HttpStatus.OK);
    }



}
