package wellness.shop.Repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wellness.shop.Models.Diet.Diet;
import wellness.shop.Models.Diet.MealPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class DietPlanRepository {


    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    ObjectMapper objectMapper;


    public DietPlanRepository(){
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public boolean registerDiet(Diet diet) {
        boolean isCreated = false;

        if (    diet == null ||
                diet.getUserUUID() == null ||
                diet.getEmployeeUUID() == null ||
                diet.getDescription() == null ||
                diet.getAge() == 0 ||
                diet.getHeight() == 0 ||
                diet.getWeight() == 0 ||
                diet.getMealPlans() == null) {
            return isCreated;
        }

        String sql = "INSERT INTO diet_plan (user_uuid, employee_uuid, description, age, height, weight, meal_plan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, diet.getUserUUID());
            preparedStatement.setString(2, diet.getEmployeeUUID());
            preparedStatement.setString(3, diet.getDescription());
            preparedStatement.setInt(4, diet.getAge());
            preparedStatement.setDouble(5, diet.getHeight());
            preparedStatement.setDouble(6, diet.getWeight());

            String mealPlansJson = generateJson(diet.getMealPlans());
            preparedStatement.setString(7, mealPlansJson);

            int rowsCreated = preparedStatement.executeUpdate();
            isCreated = rowsCreated > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return isCreated;
        }

        return isCreated;
    }

    public boolean updateDietById(int id, Diet diet) {

        boolean isUpdated = false;

        if (    id == 0 ||
                diet == null ||
                diet.getUserUUID() == null ||
                diet.getEmployeeUUID() == null ||
                diet.getDescription() == null ||
                diet.getAge() == 0 ||
                diet.getHeight() == 0 ||
                diet.getWeight() == 0 ||
                diet.getMealPlans() == null) {
            return isUpdated;
        }

        String sql = "UPDATE diet_plan SET user_uuid = ?, employee_uuid = ?, description = ?, age = ?, height = ?, weight = ?, meal_plan = ? WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, diet.getUserUUID());
            preparedStatement.setString(2, diet.getEmployeeUUID());
            preparedStatement.setString(3, diet.getDescription());
            preparedStatement.setInt(4, diet.getAge());
            preparedStatement.setDouble(5, diet.getHeight());
            preparedStatement.setDouble(6, diet.getWeight());

            String mealPlansJson = generateJson(diet.getMealPlans());
            preparedStatement.setString(7, mealPlansJson);

            preparedStatement.setInt(8, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            isUpdated = rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return isUpdated;
        }

        return isUpdated;
    }

    public List<Diet> getDietsByUserUUID(String userUUID) {

        List<Diet> diets = new ArrayList<>();

        if (userUUID == null || userUUID.isEmpty()) {
            return diets;
        }

        String sql = "SELECT * FROM diet_plan WHERE user_uuid = ?;";


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, userUUID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Diet diet = new Diet();
                diet.setId(resultSet.getInt("id"));
                diet.setUserUUID(resultSet.getString("user_uuid"));
                diet.setEmployeeUUID(resultSet.getString("employee_uuid"));
                diet.setDescription(resultSet.getString("description"));
                diet.setAge(resultSet.getInt("age"));
                diet.setHeight(resultSet.getDouble("height"));
                diet.setWeight(resultSet.getDouble("weight"));

                String mealPlanJson = resultSet.getString("meal_plan");
                List<MealPlan> mealPlans = generateObjectFromJSon(mealPlanJson, new TypeReference<List<MealPlan>>() {
                });
                diet.setMealPlans(mealPlans);

                diets.add(diet);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return diets;
        }

        return diets;
    }

    public List<Diet> getDietsByEmployeeUUID(String employeeUUID) {

        List<Diet> diets = new ArrayList<>();

        if (employeeUUID == null || employeeUUID.isEmpty()) {
            return diets;
        }

        String sql = "SELECT * FROM diet_plan WHERE employee_uuid = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, employeeUUID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Diet diet = new Diet();
                diet.setId(resultSet.getInt("id"));
                diet.setUserUUID(resultSet.getString("user_uuid"));
                diet.setEmployeeUUID(resultSet.getString("employee_uuid"));
                diet.setDescription(resultSet.getString("description"));
                diet.setAge(resultSet.getInt("age"));
                diet.setHeight(resultSet.getDouble("height"));
                diet.setWeight(resultSet.getDouble("weight"));

                String mealPlanJson = resultSet.getString("meal_plan");
                List<MealPlan> mealPlans = generateObjectFromJSon(mealPlanJson, new TypeReference<List<MealPlan>>() {});
                diet.setMealPlans(mealPlans);

                diets.add(diet);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return diets;
    }

    public boolean deleteDietById(int id) {

        boolean isDeleted = false;

        if (id == 0) {
            return isDeleted;
        }

        String sql = "DELETE FROM diet_plan WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return isDeleted;
        }

        return isDeleted;
    }



    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    private <T> T generateObjectFromJSon(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
