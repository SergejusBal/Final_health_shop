package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Security.JWT;

import java.util.HashMap;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JWT jwt;

    @GetMapping("/reload")
    public ResponseEntity<Boolean> refresh(@RequestHeader("Authorization") String authorizationHeader) {

        if(authorizationHeader == null || authorizationHeader.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }

        Role role = jwt.getRole(authorizationHeader);
        String userUUID = jwt.getUUID(authorizationHeader);

        if(role != null && userUUID != null) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }


}
