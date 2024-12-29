package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Security.JWT;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/noFilter")
public class NoFilterController {

    @Autowired
    private JWT jwt;

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
}
