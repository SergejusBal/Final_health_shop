package wellness.shop.Models;

import java.time.LocalDateTime;

public class WebSocketDTO {

    private LocalDateTime reservedTime;
    private String userUUID;

    public WebSocketDTO() {

    }

    public LocalDateTime getReservedTime() {
        return reservedTime;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setReservedTime(LocalDateTime reservedTime) {
        this.reservedTime = reservedTime;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }
}
