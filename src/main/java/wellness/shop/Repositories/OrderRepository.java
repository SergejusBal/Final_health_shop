package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wellness.shop.Models.BillingOrder.BillingOrder;
import wellness.shop.Models.BillingOrder.PaymentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class OrderRepository {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public int registerOrder(BillingOrder billingOrder){

        int orderID;

        if(     billingOrder == null                            ||      billingOrder.getCustomerName() == null          ||      billingOrder.getCustomerEmail() == null ||
                billingOrder.getCustomerAddress() == null       ||      billingOrder.getOrderCart() == null    )
        {
            return -1;
        }

        String sql = "INSERT INTO billing_order (customer_name, customer_email, customer_address, order_cart, payment_status, user_uuid, customer_phone, promo_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, billingOrder.getCustomerName());
            preparedStatement.setString(2, billingOrder.getCustomerEmail());
            preparedStatement.setString(3, billingOrder.getCustomerAddress());
            preparedStatement.setString(4, billingOrder.getOrderCart());
            preparedStatement.setString(5, PaymentStatus.PENDING.name());
            preparedStatement.setString(6, billingOrder.getUserUUID());
            preparedStatement.setString(7, billingOrder.getCustomerPhone());
            preparedStatement.setString(8, billingOrder.getPromoCode());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                orderID = resultSet.getInt(1);
            } else {
                return 0;
            }

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }

        return orderID;
     }


    public boolean updateOrder(BillingOrder billingOrder){

        if(     billingOrder == null                            ||      billingOrder.getCustomerName() == null          ||      billingOrder.getCustomerEmail() == null ||
                billingOrder.getCustomerAddress() == null       ||      billingOrder.getOrderCart() == null             ||      billingOrder.getPaymentStatus() == null )
        {
            return false;
        }

        String sql = "UPDATE billing_order SET customer_name = ?, customer_email = ?, customer_address = ?, order_cart = ?, " +
                "payment_status = ?, user_uuid = ?, customer_phone = ?, promo_code = ? WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, billingOrder.getCustomerName());
            preparedStatement.setString(2, billingOrder.getCustomerEmail());
            preparedStatement.setString(3, billingOrder.getCustomerAddress());
            preparedStatement.setString(4, billingOrder.getOrderCart());
            preparedStatement.setString(5, PaymentStatus.PENDING.name());
            preparedStatement.setString(6, billingOrder.getUserUUID());
            preparedStatement.setString(7, billingOrder.getCustomerPhone());
            preparedStatement.setString(8, billingOrder.getPromoCode());
            preparedStatement.setInt(9, billingOrder.getId());

            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    public List<BillingOrder> getAllOrders(PaymentStatus paymentStatus, int limit, int offset) {

        String paymentStatusString;

        if (paymentStatus == null) {
            paymentStatusString = "%";
        } else {
            paymentStatusString = paymentStatus.name();
        }

        if (offset < 0 || limit <= 0) {
            return null;
        }

        List<BillingOrder> orderList = new ArrayList<>();

        String sql = "SELECT * FROM billing_order WHERE payment_status LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentStatusString);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                BillingOrder billingOrder = new BillingOrder();

                billingOrder.setId(resultSet.getInt("id"));
                billingOrder.setCustomerName(resultSet.getString("customer_name"));
                billingOrder.setCustomerEmail(resultSet.getString("customer_email"));
                billingOrder.setCustomerAddress(resultSet.getString("customer_address"));
                billingOrder.setOrderCart(resultSet.getString("order_cart"));
                billingOrder.setPaymentStatus(PaymentStatus.valueOf(resultSet.getString("payment_status")));
                billingOrder.setPromoCode(resultSet.getString("promo_code"));
                billingOrder.setCustomerPhone(resultSet.getString("customer_phone"));
                billingOrder.setUserUUID(resultSet.getString("user_uuid"));

                orderList.add(billingOrder);
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

        return orderList;
    }


    public BillingOrder getOrderByID(int id){

        BillingOrder billingOrder = new BillingOrder();

        String sql = "SELECT * FROM billing_order WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,id);

            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return new BillingOrder();

            billingOrder.setId(resultSet.getInt("id"));
            billingOrder.setCustomerName(resultSet.getString("customer_name"));
            billingOrder.setCustomerEmail(resultSet.getString("customer_email"));
            billingOrder.setCustomerAddress(resultSet.getString("customer_address"));
            billingOrder.setOrderCart(resultSet.getString("order_cart"));
            billingOrder.setPaymentStatus(PaymentStatus.valueOf(resultSet.getString("payment_status")));
            billingOrder.setPromoCode(resultSet.getString("promo_code"));
            billingOrder.setCustomerPhone(resultSet.getString("customer_phone"));
            billingOrder.setUserUUID(resultSet.getString("user_uuid"));

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return new BillingOrder();
        }

        return billingOrder;
    }

    public boolean setPaymentStatus(int orderID, PaymentStatus paymentStatus){

        String sql = "UPDATE billing_order SET payment_status = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentStatus.name());
            preparedStatement.setInt(2, orderID);

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }
    }


    public PaymentStatus getPaymentStatus(int id){

        PaymentStatus paymentStatus = null;

        String sql = "SELECT payment_status FROM billing_order WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return paymentStatus;

            paymentStatus = PaymentStatus.valueOf(resultSet.getString("payment_status"));

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return paymentStatus;
        }

        return paymentStatus;

    }


    public String getProductJsonByOrderID(int id){

        String productsStringJSON = "[[]]";

        String sql = "SELECT order_cart FROM billing_order WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return productsStringJSON;

            productsStringJSON = resultSet.getString(1);

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return productsStringJSON;
        }

        return productsStringJSON;
    }

    public String getPromoCode(int id){

        String promoCode = null;

        String sql = "SELECT promo_code FROM billing_order WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return promoCode;

            promoCode = resultSet.getString("promo_code");

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return promoCode;
        }

        return promoCode;
    }

    public String getUserUUID(int id){

        String promoCode = null;

        String sql = "SELECT user_uuid FROM billing_order WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return promoCode;

            promoCode = resultSet.getString("user_uuid");

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return promoCode;
        }

        return promoCode;
    }

    public String getUserPhoneNumber(int id){

        String promoCode = null;

        String sql = "SELECT customer_phone FROM billing_order WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return promoCode;

            promoCode = resultSet.getString("customer_phone");

        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return promoCode;
        }

        return promoCode;
    }


}
