package wellness.shop.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IPRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Async
    public void registerBannedIP(String ip){

        if(ip == null){
            System.out.println("IP ban failed");
            return;
        }

        String sql = "INSERT INTO banned_ips (ip)\n" +
                "VALUES (?);";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1,ip);

            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> getBannedIPList(){

        List<String> bannedIPList = new ArrayList<>();
        String sql = "SELECT * FROM banned_ips";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()) {
                bannedIPList.add(resultSet.getString("ip"));
            }

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return bannedIPList;
    }


}
