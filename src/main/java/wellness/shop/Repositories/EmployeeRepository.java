package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wellness.shop.Models.TimeSlotOrderDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepository {


    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public List<TimeSlotOrderDTO> getOrdersByEmployeeAndDay(String employeeUUID, String paymentStatus, LocalDate timeSlotDay) {
        List<TimeSlotOrderDTO> orders = new ArrayList<>();

        String sql = "SELECT sc.time_slot, " +
                "ps.employee_uuid, " +
                "bo.id AS order_id, " +
                "bo.customer_name, " +
                "bo.customer_phone, " +
                "bo.customer_email, " +
                "bo.user_uuid," +
                "ds.product_id, " +
                "ds.payment_status " +
                "FROM health_shop.service_calendar sc " +
                "INNER JOIN health_shop.product_service ps " +
                "ON sc.employee_websocket_key = ps.employee_websocket_key " +
                "INNER JOIN health_shop.billing_order bo " +
                "ON sc.user_uuid = bo.user_uuid " +
                "INNER JOIN health_shop.diet_service ds " +
                "ON bo.id = ds.order_id " +
                "WHERE ps.employee_uuid = ? " +
                "AND ds.payment_status = ? " +
                "AND DATE(sc.time_slot) = ? " +
                "ORDER BY bo.id DESC";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, employeeUUID);
            preparedStatement.setString(2, paymentStatus);
            preparedStatement.setDate(3, java.sql.Date.valueOf(timeSlotDay));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TimeSlotOrderDTO order = new TimeSlotOrderDTO();

                LocalDateTime timeSlot = LocalDateTime.parse(resultSet.getString("time_slot"));
                order.setTimeSlot(timeSlot);

                order.setEmployeeUuid(resultSet.getString("employee_uuid"));
                order.setOrderId(resultSet.getInt("order_id"));
                order.setCustomerName(resultSet.getString("customer_name"));
                order.setCustomerPhone(resultSet.getString("customer_phone"));
                order.setCustomerEmail(resultSet.getString("customer_email"));
                order.setProductId(resultSet.getInt("product_id"));
                order.setPaymentStatus(resultSet.getString("payment_status"));
                order.setUserUUID(resultSet.getString("user_uuid"));

                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return new ArrayList<>();
        }

        return orders;
    }




}
