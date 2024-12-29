package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import wellness.shop.Models.Product;


@Repository
public class ProductRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public boolean registerProduct(Product product) {
        boolean isCreated = false;

        if (    product.getName() == null ||
                product.getDescription() == null ||
                product.getPrice() == null ||
                product.getCategory() == null ||
                product.getImageUrl() == null  ){
            return isCreated;
        }

        String sql = "INSERT INTO product (name, description, price, category, imageUrl) VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice().doubleValue());
            preparedStatement.setString(4, product.getCategory());
            preparedStatement.setString(5, product.getImageUrl());

            int rowsCreated = preparedStatement.executeUpdate();
            isCreated = rowsCreated > 0;

        } catch (SQLException e) {

            System.out.println(e.getMessage());

            return isCreated;
        }

        return isCreated;
    }

    public Product getProductById(int id) {

        Product product = null;

        String sql = "SELECT * FROM product WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(BigDecimal.valueOf(resultSet.getDouble("price")));
                product.setCategory(resultSet.getString("category"));
                product.setImageUrl(resultSet.getString("imageUrl"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return product;
        }

        return product;
    }

    public boolean updateProductById(Product product, int id) {
        boolean isUpdated = false;

        if (    product.getName() == null ||
                product.getDescription() == null ||
                product.getPrice() == null ||
                product.getCategory() == null ||
                product.getImageUrl() == null) {
            return isUpdated;
        }

        String sql = "UPDATE product SET name = ?, description = ?, price = ?, category = ?, imageUrl = ? WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice().doubleValue());
            preparedStatement.setString(4, product.getCategory());
            preparedStatement.setString(5, product.getImageUrl());
            preparedStatement.setInt(6,id);

            int rowsUpdated = preparedStatement.executeUpdate();
            isUpdated = rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return isUpdated;
    }

    public boolean deleteProductByID(int id) {

        boolean isDeleted = false;

        String sql = "DELETE FROM product WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isDeleted;
        }
        return isDeleted;
    }

    public boolean deleteProductByName(String name) {

        boolean isDeleted = false;

        String sql = "DELETE FROM product WHERE name = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isDeleted;
        }
        return isDeleted;
    }


    public int getProductIdByName(String name) {

        int productId = 0;

        String sql = "SELECT id FROM product WHERE name = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                productId = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return productId;
    }


    public List<Product> getProductsByCategory(String category, int limit, int offset) {

        List<Product> productList = new ArrayList<>();

        String sql = "SELECT * FROM product WHERE category = ? LIMIT ? OFFSET ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, category);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();

                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(BigDecimal.valueOf(resultSet.getDouble("price")));
                product.setCategory(resultSet.getString("category"));
                product.setImageUrl(resultSet.getString("imageUrl"));

                productList.add(product);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return productList;
    }


    public List<Product> getProductsByName(String name, int limit, int offset) {

        List<Product> productList = new ArrayList<>();

        String sql = "SELECT * FROM product WHERE name LIKE ? LIMIT ? OFFSET ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + name + "%");
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();

                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(BigDecimal.valueOf(resultSet.getDouble("price")));
                product.setCategory(resultSet.getString("category"));
                product.setImageUrl(resultSet.getString("imageUrl"));

                productList.add(product);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return productList;
    }


    public BigDecimal getPriceById(int id) {

        BigDecimal price = null;

        String sql = "SELECT price FROM product WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                price = BigDecimal.valueOf(resultSet.getDouble("price"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return price;
    }


}
