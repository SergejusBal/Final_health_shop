package wellness.shop.Services;

import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Models.Users.Guest;
import wellness.shop.Models.Users.User;
import wellness.shop.Repositories.UserRepository;
import wellness.shop.Security.JWT;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.util.HashMap;
import java.util.function.Function;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWT jwt;


    public String registerUser(Guest user) {

        if(!UtilitiesGeneral.isValidEmail(user.getUsername()))  return "Invalid data";

        return userRepository.registerUser(user);
    }


    public HashMap<String,String> login(Guest guestUser) {

        HashMap<String, String> response = new HashMap<>();

        User user = userRepository.getUserInfo(guestUser.getUsername());

        if(BCrypt.checkpw(guestUser.getPassword(), user.getPassword())){
            response.put("status","User authorize");
            response.put("JWToken", jwt.generateJwt(user.getUUID(),user.getRole()));
            return response;
        }
        response.put("status","No authorization");

        return response;
    }

    public String deleteUser(String username, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID ->{
                   return  userRepository.deleteUser(userUUID);
                },
                "User was deleted",
                "User not found"
        );
    }

    public String changeUserRole(String username, Role role, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID ->{
                    return userRepository.changeUserRole(userUUID, role);
                },
                "User role was changed",
                "User not found"
        );
    }

    public String createPrivilege(String username, Privileges privileges, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    userRepository.deletePrivilege(userUUID, privileges);
                    return userRepository.createPrivilege(userUUID, privileges);
                },
                "User privilege was added",
                "User not found"
        );
    }

    public String deletePrivilege(String username, Privileges privileges, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    return userRepository.deletePrivilege(userUUID, privileges);
                },
                "User privilege was deleted",
                "User or privilege not found"
        );
    }

    public String createSpecialization(String username, Specialization specialization, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    userRepository.deleteSpecialization(userUUID, specialization);
                    return userRepository.createSpecialization(userUUID, specialization);
                },
                "User specialization was added",
                "User not found"
        );
    }

    public String deleteSpecialization(String username, Specialization specialization, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    return userRepository.deleteSpecialization(userUUID, specialization);
                },
                "User specialization was deleted",
                "User or specialization not found"
        );
    }

    public String createSubscription(String username, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    userRepository.deleteSubscription(userUUID);
                    return userRepository.createSubscription(userUUID);
                },
                "User subscription was added",
                "User not found"
        );
    }

    public String modifySubscription(String username, Boolean status, String authorizationHeader){
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    return userRepository.modifySubscription(userUUID, status);
                },
                "User subscription was changed",
                "User or subscription not found"
        );


    }

    public String deleteSubscription(String username, String authorizationHeader) {
        if (!isAuthorized(authorizationHeader,Privileges.MODIFY_USERS)) {
            return "No authorization";
        }

        return handleUserAction(
                username,
                userUUID -> {
                    return userRepository.deleteSubscription(userUUID);
                },
                "User subscription was deleted",
                "User subscription not found"
        );
    }



    public boolean isAuthorized(String authorizationHeader, Privileges privileges) {
        if (StringUtils.startsWithIgnoreCase(authorizationHeader, "Bearer ")) {
            authorizationHeader = authorizationHeader.substring(7);
        } else {
            return false;
        }

        Claims claims = jwt.decodeJwt(authorizationHeader);
        String adminUUID = claims.get("userUUID", String.class);

        return userRepository.checkPrivilege(adminUUID, privileges);
    }

    public boolean isAuthorized(String authorizationHeader, Specialization specialization) {
        if (StringUtils.startsWithIgnoreCase(authorizationHeader, "Bearer ")) {
            authorizationHeader = authorizationHeader.substring(7);
        } else {
            return false;
        }

        Claims claims = jwt.decodeJwt(authorizationHeader);
        String employeeUUID = claims.get("userUUID", String.class);

        return userRepository.checkSpecialization(employeeUUID, specialization);
    }

    private String handleUserAction(String username, Function<String, Boolean> action, String successMessage, String failureMessage) {
        String userUUID = userRepository.getUuidByUsername(username);

        if (userUUID == null) {
            return failureMessage;
        }

        return action.apply(userUUID) ? successMessage : failureMessage;
    }



}
