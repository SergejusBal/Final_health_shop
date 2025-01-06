package wellness.shop.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import wellness.shop.Models.WebSocketDTO;
import wellness.shop.Services.DateRegistrationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DateRegistrationController {

    @Autowired
    private DateRegistrationService dateRegistrationService;

    @GetMapping("/date/get/{webSocketKey}")
    public ResponseEntity<HashMap<LocalDateTime,String>> getDates(@PathVariable String webSocketKey){

        HashMap<LocalDateTime,String> calendar =  dateRegistrationService.getDates(webSocketKey);

        if(calendar.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(calendar,HttpStatus.OK);
    }

    @MessageMapping("/date/{webSocketKey}")
    @SendTo("/topic/date/{webSocketKey}")
    public WebSocketDTO dateEcho(@DestinationVariable String webSocketKey, @RequestBody WebSocketDTO webSocketDTO, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

        String authorization = (String) sessionAttributes.get("Authorization");

        if(dateRegistrationService.registerDate(webSocketKey,webSocketDTO,authorization)) return webSocketDTO;

        return new WebSocketDTO();
    }

}
