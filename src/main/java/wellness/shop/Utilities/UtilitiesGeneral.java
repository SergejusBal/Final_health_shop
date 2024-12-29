package wellness.shop.Utilities;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class UtilitiesGeneral {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private static final Map<String, HttpStatus> RESPONSE_STATUS_MAP = Map.ofEntries(
            Map.entry("Invalid username or password", HttpStatus.UNAUTHORIZED),
            Map.entry("No authorization", HttpStatus.UNAUTHORIZED),
            Map.entry("Database connection failed", HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry("Invalid data", HttpStatus.BAD_REQUEST),
            Map.entry("User already exists", HttpStatus.CONFLICT),
            Map.entry("User authorize", HttpStatus.OK),
            Map.entry("User not found", HttpStatus.NOT_FOUND),
            Map.entry("User or privilege not found", HttpStatus.NOT_FOUND),
            Map.entry("User or specialization not found", HttpStatus.NOT_FOUND),
            Map.entry("User subscription not found", HttpStatus.NOT_FOUND),
            Map.entry("User or subscription not found", HttpStatus.NOT_FOUND),
            Map.entry("User was successfully added", HttpStatus.OK),
            Map.entry("User was deleted", HttpStatus.OK),
            Map.entry("User subscription was deleted", HttpStatus.OK),
            Map.entry("User role was changed", HttpStatus.OK),
            Map.entry("User subscription was changed", HttpStatus.OK),
            Map.entry("User privilege was deleted", HttpStatus.OK),
            Map.entry("User specialization was deleted", HttpStatus.OK),
            Map.entry("User subscription was added", HttpStatus.OK),
            Map.entry("User privilege was added", HttpStatus.OK),
            Map.entry("User specialization was added", HttpStatus.OK),
            Map.entry("Email already exists", HttpStatus.CONFLICT),
            Map.entry("Email was successfully added", HttpStatus.OK)
    );


    public static HttpStatus checkHttpStatus(String response) {
        return RESPONSE_STATUS_MAP.getOrDefault(response, HttpStatus.NOT_IMPLEMENTED);
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static LocalDateTime formatDateTime(String dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime rentalDate = null;
        try {
            rentalDate = LocalDateTime.parse(dateTime, dateTimeFormatter);
        }catch(DateTimeParseException | NullPointerException e) {
            rentalDate = LocalDateTime.parse("1900-01-01 00:00:00", dateTimeFormatter);
        }
        return rentalDate;
    }

    public static LocalDate formatDate(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate formattedDate = null;
        try {
            formattedDate = LocalDate.parse(date, dateFormatter);
        } catch (DateTimeParseException | NullPointerException e) {
            formattedDate = LocalDate.parse("1900-01-01", dateFormatter);
        }
        return formattedDate;
    }

    public static UUID generateUID(){
        return UUID.randomUUID();
    }




}
