package wellness.shop.Services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wellness.shop.Integration.Redis;
import wellness.shop.Models.Diet.Diet;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Repositories.DietRepository;
import wellness.shop.Security.JWT;

import java.util.List;
import java.util.function.Supplier;

@Service
public class DietService {

    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;

    private Redis redis;

    @Autowired
    private DietRepository dietRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JWT jwt;

    @PostConstruct
    public void init(){
        this.redis = new Redis(redisHost, redisPort);
    }

    public List<Diet> getAllYourDiets(String authorizationHeader){

        String userUUID = jwt.getUUID(authorizationHeader);
        String userName;

        userName = redis.get(userUUID);
        if(userName == null){
            userName = userService.getUserNameByUUID(userUUID);
            redis.put(userUUID,userName,600);
        }

        return dietRepository.getDietsByUser(userName);
    }

    public Diet getDietByID(int dietID,String authorizationHeader){

       String userUUID = jwt.getUUID(authorizationHeader);
       String userName;

       userName = redis.get(userUUID);
       if(userName == null){
           userName = userService.getUserNameByUUID(userUUID);
           redis.put(userUUID, userName,600);
       }

       String userNameInData = dietRepository.getUserByDietID(dietID);

       if(userNameInData == null) return new Diet(0);

       if(!userName.equals(userNameInData)) return null;
       else return dietRepository.getDietById(dietID);
    }


    public List<Diet> getAllDietsByUsername(String username, String authorizationHeader){

        if (    !(userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN) ||
                userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS)))
        {
            return null;
        }

        return dietRepository.getDietsByUser(username);
    }

    public List<Diet> getAllDietsEmployeeDiets(String authorizationHeader){

        if (    !(userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN) ||
                userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS)))
        {
            return null;
        }

        String employeeUUID = jwt.getUUID(authorizationHeader);
        String employeeName;

        employeeName = redis.get(employeeUUID);
        if(employeeName == null){
            employeeName = userService.getUserNameByUUID(employeeUUID);
            redis.put(employeeUUID,employeeName,600);
        }

        return dietRepository.getDietsByEmployee(employeeName);
    }

    public String registerDiet(Diet diet, String authorizationHeader){
        if (    !(userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN) ||
                userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS)))
         {
            return "No authorization";
        }

        String employeeUUID = jwt.getUUID(authorizationHeader);
        String employee = userService.getUserNameByUUID(employeeUUID);
        diet.setEmployee(employee);

        return handleUserAction(
                ()->{
                    return dietRepository.registerDiet(diet);
                },
                "Diet was successfully added",
                "Fail to add diet"
        );

    }

    public String updateDiet(Diet diet, int id, String authorizationHeader){

        if (    !(userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN) ||
                userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS)))
        {
            return "No authorization";
        }

        String employeeUUID = jwt.getUUID(authorizationHeader);
        String employee = dietRepository.getEmployeeByDietID(id);
        String confirmedEmployeeUUID = userService.getUserUUIDByName(employee);

        if(employeeUUID == null || !employeeUUID.equals(confirmedEmployeeUUID)) return "No authorization";

        diet.setEmployee(employee);
        return handleUserAction(
                ()->{
                    return dietRepository.updateDietById(id,diet);
                },
                "Diet was modified",
                "Diet not found"
        );

    }

    public String deleteDietPlanEmployee(int id, String authorizationHeader){

        if (!userService.isAuthorized(authorizationHeader, Specialization.DIETITIAN))
        {
            return "No authorization";
        }

        String employeeUUID = jwt.getUUID(authorizationHeader);
        String employee = dietRepository.getEmployeeByDietID(id);
        String confirmedEmployeeUUID = userService.getUserUUIDByName(employee);

        if(employeeUUID == null || !employeeUUID.equals(confirmedEmployeeUUID)) return "No authorization";

        return handleUserAction(
                ()->{
                    return dietRepository.deleteDietById(id);
                },
                "Diet was successfully deleted",
                "Diet not found"
        );

    }

    public String deleteDietPlanAdmin(int id, String authorizationHeader){

        if (!userService.isAuthorized(authorizationHeader, Privileges.MODIFY_DIET_PLANS))
        {
            return "No authorization";
        }

        return handleUserAction(
                ()->{
                    return dietRepository.deleteDietById(id);
                },
                "Diet was successfully deleted",
                "Diet not found"
        );

    }

    private String handleUserAction(Supplier<Boolean> action, String successMessage, String failureMessage) {
        return action.get() ? successMessage : failureMessage;
    }


}
