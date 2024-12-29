package wellness.shop.Repositories;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wellness.shop.Models.FoodItem;


import java.sql.*;

@Repository
public class FoodRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    public boolean registerFoodItem(FoodItem foodItem) {

        boolean isCreated = false;

        if(     foodItem.getFood() == null ||
                foodItem.getCalories() == null ||
                foodItem.getFats() == null ||
                foodItem.getProteins() == null ||
                foodItem.getCarbohydrates() == null) return isCreated;

        String sql = "INSERT INTO food_table (food, calories, proteins, fats, carbohydrates, fibers) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, foodItem.getFood());
            preparedStatement.setDouble(2, foodItem.getCalories());
            preparedStatement.setDouble(3, foodItem.getProteins());
            preparedStatement.setDouble(4, foodItem.getFats());
            preparedStatement.setDouble(5, foodItem.getCarbohydrates());
            preparedStatement.setDouble(6, foodItem.getFibers());

            int rowsCreated = preparedStatement.executeUpdate();
            isCreated = rowsCreated > 0;

        }catch (SQLException e) {

            System.out.println("SQL Error: " + e.getMessage());

            if (e.getErrorCode() == 1062) return isCreated;

            return isCreated;
        }

        return isCreated;

    }

    public boolean updateFoodItemByFoodName(FoodItem foodItem) {

        boolean isUpdated = false;

        if (foodItem.getFood() == null ||
                foodItem.getCalories() == null ||
                foodItem.getFats() == null ||
                foodItem.getProteins() == null ||
                foodItem.getCarbohydrates() == null) {
            return isUpdated;
        }

        String sql = "UPDATE food_table SET calories = ?, proteins = ?, fats = ?, carbohydrates = ?, fibers = ? WHERE food = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, foodItem.getCalories());
            preparedStatement.setDouble(2, foodItem.getProteins());
            preparedStatement.setDouble(3, foodItem.getFats());
            preparedStatement.setDouble(4, foodItem.getCarbohydrates());
            preparedStatement.setDouble(5, foodItem.getFibers());
            preparedStatement.setString(6, foodItem.getFood());

            int rowsUpdated = preparedStatement.executeUpdate();
            isUpdated = rowsUpdated > 0;

        } catch (SQLException e) {

            System.out.println("SQL Error: " + e.getMessage());

            return isUpdated;
        }

        return isUpdated;
    }


    public FoodItem getFoodItemByFoodName(String foodName) {

        if (foodName == null || foodName.isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM food_table WHERE food = ?;";

        FoodItem foodItem = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, foodName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                foodItem = new FoodItem();
                foodItem.setFood(resultSet.getString("food"));
                foodItem.setCalories(resultSet.getDouble("calories"));
                foodItem.setProteins(resultSet.getDouble("proteins"));
                foodItem.setFats(resultSet.getDouble("fats"));
                foodItem.setCarbohydrates(resultSet.getDouble("carbohydrates"));
                foodItem.setFibers(resultSet.getDouble("fibers"));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return foodItem;
        }

        return foodItem;
    }

    public boolean deleteFood(String foodName) {

        if (foodName == null) return false;

        boolean isDeleted = false;

        String sql = "DELETE FROM food_table WHERE food = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, foodName);

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return isDeleted;
        }
        return isDeleted;
    }


}
