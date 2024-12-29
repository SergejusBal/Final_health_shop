package wellness.shop.Repositories;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wellness.shop.Models.Users.*;
import wellness.shop.Models.Users.Enums.Privileges;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Models.Users.Enums.Specialization;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository{


    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    public String registerUser(Guest user) {

        if(user.getUsername() == null || user.getPassword() == null) return "Invalid data";

        String sql = "INSERT INTO users (uuid,username,password,role)\n" +
                "VALUES (?,?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, UtilitiesGeneral.generateUID().toString());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            preparedStatement.setString(4, Role.REGULAR.name());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            if (e.getErrorCode() == 1062) return "User already exists";

            return "Database connection failed";
        }
        return "User was successfully added";

    }

    public boolean changeUserRole(String uuid, Role role) {
        if (uuid == null || role == null) {
            return false;
        }

        String sql = "UPDATE users SET role = ? WHERE uuid = ?;";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, role.name());
            preparedStatement.setString(2, uuid);

            int rowsUpdated = preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User getUserInfo(String userName){

        User user = null;

        if(userName == null) return null;

        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return null;

            user = createUser(resultSet);

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return user;

    }

    public String getUuidByUsername(String name) {

        if (name == null || name.isEmpty()) return null;

        String sql = "SELECT uuid FROM users WHERE username = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getString("uuid");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return null;

    }



    public User getUserInfoFromUUID(String uuid){

        User user = null;

        if(uuid == null) return null;

        String sql = "SELECT * FROM users WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,uuid);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return null;

            user = createUser(resultSet);

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return user;

    }

    public boolean checkSubscription(String uuid){

        boolean status = false;

        if(uuid == null) return status;

        String sql = "SELECT * FROM subscription WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,uuid);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return status;

            status = resultSet.getBoolean("status");

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return status;
        }

        return status;

    }

    public boolean modifySubscription(String uuid, boolean newStatus) {

        boolean isModified = false;

        if (uuid == null) return isModified;

        String sql = "UPDATE subscription SET status = ? WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBoolean(1, newStatus);
            preparedStatement.setString(2, uuid);

            int rowsAffected = preparedStatement.executeUpdate();
            isModified = rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isModified;
        }

        return isModified;
    }

    public boolean createSubscription(String uuid) {
        boolean isCreated = false;

        if (uuid == null) return isCreated;

        String sql = "INSERT INTO subscription (uuid, status) VALUES (?, ?)";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);
            preparedStatement.setBoolean(2, true);

            int rowsCreated = preparedStatement.executeUpdate();
            isCreated = rowsCreated > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isCreated;
        }

        return isCreated;
    }

    public boolean deleteSubscription(String uuid) {



        if (uuid == null) return false;

        String sql = "DELETE FROM subscription WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);

            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }



    public List<Privileges> getPrivileges(String uuid){

        List<Privileges> privileges = new ArrayList<>();

        if(uuid == null) return privileges;

        String sql = "SELECT * FROM admin_info WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,uuid);
            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()){
                privileges.add(Privileges.valueOf(resultSet.getString("privilege")));
            }


        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return privileges;
        }

        return privileges;
    }

    public boolean checkPrivilege(String uuid, Privileges privilege){

        if(uuid == null || privilege == null) return false;

        String sql = "SELECT 1 FROM admin_info WHERE uuid = ? AND privilege = ? LIMIT 1";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,uuid);
            preparedStatement.setString(2,privilege.name());
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(resultSet.next()) return true;

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return false;
    }

    public boolean createPrivilege(String uuid, Privileges privilege) {
        boolean isCreated = false;

        if (uuid == null || privilege == null) return isCreated;

        String sql = "INSERT INTO admin_info (uuid, privilege) VALUES (?, ?)";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, privilege.name());

            int rowsInserted = preparedStatement.executeUpdate();
            isCreated = rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isCreated;
        }

        return isCreated;
    }

    public boolean deletePrivilege(String uuid, Privileges privilege) {
        boolean isDeleted = false;

        if (uuid == null || privilege == null) return isDeleted;

        String sql = "DELETE FROM admin_info WHERE uuid = ? AND privilege = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, privilege.name());

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isDeleted;
        }

        return isDeleted;
    }

    public void deletePrivilege(String uuid) {

        if (uuid == null) return;

        String sql = "DELETE FROM admin_info WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public List<Specialization> getSpecialization(String uuid){

        List<Specialization> specialization = new ArrayList<>();

        if(uuid == null) return specialization;

        String sql = "SELECT * FROM employee_info WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,uuid);
            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()){
                specialization.add(Specialization.valueOf(resultSet.getString("privilege")));
            }


        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return specialization;
        }

        return specialization;
    }

    public boolean createSpecialization(String uuid, Specialization specialization) {
        boolean isCreated = false;

        if (uuid == null || specialization == null) return isCreated;

        String sql = "INSERT INTO employee_info (uuid, specialization) VALUES (?, ?)";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, specialization.name());

            int rowsInserted = preparedStatement.executeUpdate();
            isCreated = rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isCreated;
        }

        return isCreated;
    }

    public boolean deleteSpecialization(String uuid, Specialization specialization) {
        boolean isDeleted = false;

        if (uuid == null || specialization == null) return isDeleted;

        String sql = "DELETE FROM employee_info WHERE uuid = ? AND specialization = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, specialization.name());

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isDeleted;
        }

        return isDeleted;
    }

    public void deleteSpecialization(String uuid) {

        if (uuid == null) return;

        String sql = "DELETE FROM employee_info WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public boolean deleteUser(String uuid) {

        boolean isDeleted = false;
        if (uuid == null) return isDeleted;

        deleteSubscription(uuid);
        deleteSpecialization(uuid);
        deletePrivilege(uuid);

        String sql = "DELETE FROM users WHERE uuid = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);

            int rowsDeleted = preparedStatement.executeUpdate();
            isDeleted = rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return isDeleted;
        }

        return isDeleted;
    }


    private User createUser(ResultSet resultSet) throws SQLException {

        User user = null;

        String uuid = resultSet.getString("uuid");
        String username = resultSet.getString("username");
        String hashedPassword = resultSet.getString("password");
        Role role = Role.valueOf(resultSet.getString("role"));

        switch(role){
            case Role.REGULAR:
                user = new RegularUser();
                ((RegularUser)user).setSubscription(checkSubscription(uuid));
                break;

            case Role.EMPLOYEE:
                user = new Employee();
                ((Employee)user).setSpecializations(getSpecialization(uuid));
                break;

            case Role.ADMIN:
                user = new Admin();
                ((Admin)user).setPrivileges(getPrivileges(uuid));
                break;

            default:
                user = new Guest();
                break;
        }

        user.setUUID(uuid);
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole(role);

        return user;

    }






}