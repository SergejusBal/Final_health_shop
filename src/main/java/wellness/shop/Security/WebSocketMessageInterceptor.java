package wellness.shop.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import wellness.shop.Models.Users.Enums.Role;

@Component
public class WebSocketMessageInterceptor implements ChannelInterceptor {

    @Autowired
    private JWT jwt;
    Role[] allowedUserRoles = {Role.ADMIN, Role.EMPLOYEE, Role.REGULAR};

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(accessor == null) return message;

        String authorizationHeader;

        if (accessor.getCommand() == StompCommand.CONNECT) {
            authorizationHeader = accessor.getFirstNativeHeader("Authorization");
            accessor.getSessionAttributes().put("Authorization", authorizationHeader);
        }

        if(accessor.getCommand() == StompCommand.DISCONNECT) return message;

        authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        if(jwt.checkRolesAndValidateToken(authorizationHeader,allowedUserRoles)) return message;

        return null;
    }


    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    }
    @Override
    public boolean preReceive(MessageChannel channel) {
        return true;
    }
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return message;
    }


//            preSend(): Intercepts the message before it is sent from the client to the server or from the server to clients.
//            postSend(): Occurs after the message has been sent to the message broker or client.
//            preReceive(): Happens before a message is received from the message broker by the server.
//            postReceive(): Takes place after the message is received but before it is processed by the server's business logic.

}
