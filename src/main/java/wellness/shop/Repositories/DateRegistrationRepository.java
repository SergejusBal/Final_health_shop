package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class DateRegistrationRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void registerRegistrationDate(LocalDateTime localDateTime, String webSocketKey) {

        String sql = "INSERT INTO service_calendar (time_slot, employee_websocket_key) VALUES (?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, localDateTime.toString());
            preparedStatement.setString(2, webSocketKey);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public boolean registerUserUUID(String userUUID, String webSocketKey, LocalDateTime localDateTime) {

        String sql = "UPDATE service_calendar SET user_uuid = ? WHERE employee_websocket_key = ? AND time_slot = ?;";


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String formattedTimeSlot = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            preparedStatement.setString(1, userUUID);
            preparedStatement.setString(2, webSocketKey);
            preparedStatement.setString(3, formattedTimeSlot);

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());

            return false;
        }
    }

    public String getRegistrationUserUUID(LocalDateTime localDateTime, String webSocketKey) {

        String sql = "SELECT user_uuid FROM service_calendar " +
                "WHERE time_slot = ? AND employee_websocket_key = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String formattedTimeSlot = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            preparedStatement.setString(1, formattedTimeSlot);
            preparedStatement.setString(2, webSocketKey);

            ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("user_uuid");
                } else {
                    return null;
                }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return null;
        }
    }


    public boolean doesDateExist(LocalDateTime localDateTime, String webSocketKey) {

        String sql = "SELECT * FROM service_calendar WHERE time_slot = ? AND employee_websocket_key = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, localDateTime.toString());
            preparedStatement.setString(2, webSocketKey);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }
    }

    public boolean doesRegistrationExist(LocalDate startDate, LocalDate endDate, String webSocketKey, String userUUID) {

        String sql = "SELECT * FROM service_calendar " +
                "WHERE employee_websocket_key = ? AND user_uuid = ?" +
                "AND time_slot >= ? " +
                "AND time_slot < ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, webSocketKey);
            preparedStatement.setString(2, userUUID);
            preparedStatement.setString(3, startDate.atStartOfDay().toString());
            preparedStatement.setString(4, endDate.plusDays(1).atStartOfDay().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }
    }


    public HashMap<LocalDateTime,String> getTimeSlotsWithinDateRange(LocalDate startDate, LocalDate endDate, String webSocketKey) {

        String sql = "SELECT time_slot, user_uuid FROM service_calendar " +
                     "WHERE employee_websocket_key = ? " +
                     "AND time_slot >= ? " +
                     "AND time_slot < ?";

        HashMap<LocalDateTime,String> timeSlots = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, webSocketKey);
            preparedStatement.setString(2, startDate.atStartOfDay().toString());
            preparedStatement.setString(3, endDate.plusDays(1).atStartOfDay().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                timeSlots.put(LocalDateTime.parse(resultSet.getString("time_slot")),resultSet.getString("user_uuid"));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return timeSlots;
    }



    public List<String> getUniqueWebsocketKeys() {
        List<String> websocketKeys = new ArrayList<>();

        String sql = "SELECT DISTINCT employee_websocket_key FROM product_service";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                websocketKeys.add(resultSet.getString("employee_websocket_key"));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return websocketKeys;
        }

        return websocketKeys;
    }






}
