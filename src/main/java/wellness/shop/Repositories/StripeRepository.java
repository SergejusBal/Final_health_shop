package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.UUID;

@Repository
public class StripeRepository {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    public int createPayment(String userUUID, String secretUUID, Integer orderID){

        int paymentID = 0;

        String sql = "INSERT INTO stripe_payment (order_id, user_uuid, payment_secret_uuid)\n" +
                "VALUES (?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1,orderID);
            preparedStatement.setString(2,userUUID);
            preparedStatement.setString(3,secretUUID);

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                paymentID = resultSet.getInt(1);
            } else {
                return 0;
            }


        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return paymentID;
        }
        return paymentID;
    }


    public int getOrderID(int paymentID, String userUUID){

        int orderID = 0;

        String sql = "SELECT * FROM stripe_payment WHERE id = ? AND user_uuid = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,paymentID);
            preparedStatement.setString(2,userUUID);

            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return orderID;

            orderID = resultSet.getInt("order_id");

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return orderID;
        }

    return orderID;
    }

    public int getOrderIDByRefundKey(String refundKey){
        int orderID = 0;
        String sql = "SELECT order_id FROM stripe_payment WHERE refund_key = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1,refundKey);

            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return orderID;

            orderID = resultSet.getInt("order_id");


        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return orderID;
        }
        return orderID;
    }

    public String getPaymentIntentIDByRefundKey(String refundKey){

        String paymentIntentID = null;

        String sql = "SELECT payment_intent_id FROM stripe_payment WHERE refund_key = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1,refundKey);

            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return paymentIntentID;

            paymentIntentID = resultSet.getString("order_id");


        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return paymentIntentID;
        }
        return paymentIntentID;
    }


    public String getPaymentIntentID(int paymentID){

        String paymentIntentID = null;

        String sql = "SELECT payment_intent_id FROM stripe_payment WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,paymentID);

            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return paymentIntentID;

            paymentIntentID = resultSet.getString("payment_intent_id");

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return paymentIntentID;
        }

        return paymentIntentID;
    }

    public boolean setPaymentIntentID(int paymentID, String paymentIntentID){

        boolean isUpdated = false;

        String sql = "UPDATE stripe_payment SET payment_intent_id = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentIntentID);
            preparedStatement.setInt(2, paymentID);

            int rowsUpdated = preparedStatement.executeUpdate();
            isUpdated = rowsUpdated > 0;


        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return isUpdated;
        }
        return isUpdated;
    }

    public boolean setRefundKey(int paymentID, String refundKey){

        boolean isUpdated = false;

        String sql = "UPDATE stripe_payment SET refund_key = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, refundKey);
            preparedStatement.setInt(2, paymentID);

            int rowsUpdated = preparedStatement.executeUpdate();
            isUpdated = rowsUpdated > 0;

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return isUpdated;
        }
        return isUpdated;
    }

    public boolean checkIfPaymentValid(int paymentID, String userUUI, String secretUUID){

        String sql = "SELECT * FROM stripe_payment WHERE id = ? AND user_uuid = ? AND payment_secret_uuid = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,paymentID);
            preparedStatement.setString(2,userUUI);
            preparedStatement.setString(3,secretUUID);

            ResultSet resultSet =  preparedStatement.executeQuery();

            return resultSet.next();

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }


}
