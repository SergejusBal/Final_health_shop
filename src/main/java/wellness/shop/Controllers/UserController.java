package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Security.JWT;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JWT jwt;
    @PostMapping("/test")
    public ResponseEntity<String> Test() {
        return new ResponseEntity<>(JWT.generateJwt("Test", Role.ADMIN), HttpStatus.OK);
    }
}
