package wellness.shop.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.Diet.Diet;
import wellness.shop.Services.DietService;
import wellness.shop.Utilities.UtilitiesGeneral;


import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/diet")
public class DietController {

    @Autowired
    private DietService dietService;

    @GetMapping("/users/get")
    public ResponseEntity<List<Diet>> getAllYourDiets(@RequestHeader("Authorization") String authorizationHeader){

       List<Diet> dietList =  dietService.getAllYourDiets(authorizationHeader);

       if(dietList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       else return new ResponseEntity<>(dietList,HttpStatus.OK);
    }

    @GetMapping("/users/get/{dietID}")
    public ResponseEntity<Diet> getDietByID(@PathVariable int dietID, @RequestHeader("Authorization") String authorizationHeader){

       Diet diet =  dietService.getDietByID(dietID,authorizationHeader);

       if(diet == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
       else if (diet.getId() == 0) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       else return new ResponseEntity<>(diet,HttpStatus.OK);

    }

    @GetMapping("/secured/user/diet/{username}")
    public ResponseEntity<List<Diet>> getAllDietsByUsername(@PathVariable String username, @RequestHeader("Authorization") String authorizationHeader){

        List<Diet> dietList = dietService.getAllDietsByUsername(username,authorizationHeader);

        if(dietList == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (dietList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(dietList,HttpStatus.OK);
    }

    @GetMapping("/secured/employee/diet")
    public ResponseEntity<List<Diet>> getAllDietsEmployeeDiets(@RequestHeader("Authorization") String authorizationHeader){

        List<Diet> dietList = dietService.getAllDietsEmployeeDiets(authorizationHeader);

        if(dietList == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else if (dietList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(dietList,HttpStatus.OK);
    }

    @PostMapping("/secured/new")
    public ResponseEntity<String> registerDiet(@RequestBody Diet diet, @RequestHeader("Authorization") String authorizationHeader){
        String response = dietService.registerDiet(diet,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @PutMapping("/secured/update/{id}")
    public ResponseEntity<String> updateDiet(@RequestBody Diet diet, @PathVariable int id, @RequestHeader("Authorization") String authorizationHeader){
        String response = dietService.updateDiet(diet,id,authorizationHeader);
        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }

    @DeleteMapping("/secured/{role}/delete/diet/{id}")
    public ResponseEntity<String> deleteDietPlan(@PathVariable String role, @PathVariable int id, @RequestHeader("Authorization") String authorizationHeader) {

        String response;
        if ("employee".equalsIgnoreCase(role)) {
            response = dietService.deleteDietPlanEmployee(id, authorizationHeader);
        } else if ("admin".equalsIgnoreCase(role)) {
            response = dietService.deleteDietPlanAdmin(id, authorizationHeader);
        } else {
            return new ResponseEntity<>("Invalid role", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(response, UtilitiesGeneral.checkHttpStatus(response));
    }



}
