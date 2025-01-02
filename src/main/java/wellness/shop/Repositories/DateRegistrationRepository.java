package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<LocalDateTime> getTimeSlotsWithinDateRange(LocalDate startDate, LocalDate endDate, String webSocketKey) {

        String sql = "SELECT time_slot FROM service_calendar " +
                     "WHERE employee_websocket_key = ? " +
                     "AND time_slot >= ? " +
                     "AND time_slot < ?";

        List<LocalDateTime> timeSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, webSocketKey);
            preparedStatement.setString(2, startDate.atStartOfDay().toString());
            preparedStatement.setString(3, endDate.plusDays(1).atStartOfDay().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                timeSlots.add(LocalDateTime.parse(resultSet.getString("time_slot")));
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
