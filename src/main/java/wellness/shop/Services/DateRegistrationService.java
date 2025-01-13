package wellness.shop.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import wellness.shop.Models.WebSocketDTO;
import wellness.shop.Repositories.DateRegistrationRepository;
import wellness.shop.Security.JWT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class DateRegistrationService {

    @Autowired
    private DateRegistrationRepository dateRegistrationRepository;
    @Autowired
    private JWT jwt;

    public HashMap<LocalDateTime,String> getDates(String webSocketKey) {

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dateAfter29Days = today.plusDays(29);

        return dateRegistrationRepository.getTimeSlotsWithinDateRange(tomorrow,dateAfter29Days,webSocketKey);

    }

    public boolean registerDate(String webSocketKey, WebSocketDTO webSocketDTO, String authorizationHeader){

        String userUUID = jwt.getUUID(authorizationHeader);

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dateAfter29Days = today.plusDays(29);

        // check if date is empty
        String registeredUserUUID = dateRegistrationRepository.getRegistrationUserUUID(webSocketDTO.getReservedTime(),webSocketKey);

        // remove registration if it exists
        if(registeredUserUUID != null && registeredUserUUID.equals(userUUID)){
            return dateRegistrationRepository.registerUserUUID(null,webSocketKey,webSocketDTO.getReservedTime());
        }
        // check if there is a registration upcoming  weeks
        if (dateRegistrationRepository.doesRegistrationExist(tomorrow,dateAfter29Days,webSocketKey,userUUID)) return false;

        // if free a date is registered
        if(registeredUserUUID == null || registeredUserUUID.isEmpty()){
            return dateRegistrationRepository.registerUserUUID(userUUID,webSocketKey,webSocketDTO.getReservedTime());
        }

        return false;

    }





}
