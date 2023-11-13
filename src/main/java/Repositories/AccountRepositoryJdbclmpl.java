package Repositories;

import Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRepositoryJdbclmpl implements AccountRepository{

    private final Connection connection;
    private static final String SQL_INSERT_USER = "insert into users (user_uuid,username, email, password) values (?,?,?,?) ";

    public AccountRepositoryJdbclmpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User user) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_USER);
        UUID userUuid = UUID.randomUUID();
        preparedStatement.setObject(1, userUuid);
        preparedStatement.setString(2, user.getUserName());
        preparedStatement.setString(3, user.getUserEmail());
        preparedStatement.setString(4, user.getUserPassword());

        preparedStatement.executeUpdate();
        System.out.println("Executed");

    }

    @Override
    public boolean login(String username, String password, User user) throws SQLException {
        String sql = "select username, email, password from users where username = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getUserName());
        ResultSet resultSet = preparedStatement.executeQuery();

        String accountUsername = "";
        String accountEmail = "";
        String accountPassword = "";

        while (resultSet.next()) {
            accountUsername = resultSet.getString("username");
            accountEmail = resultSet.getString("email");
            accountPassword = resultSet.getString("password");
        }

        user.setUserName(accountUsername);
        user.setUserEmail(accountEmail);
        user.setUserPassword(accountPassword);

        return user.getUserName().equals(username) && user.getUserPassword().equals(password);
    }

    @Override
    public boolean findUUID(UUID uuid) throws SQLException {
        String sql = "select count(*) from users where user_uuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UUID getUserUUID(String username) throws SQLException {
        String sql = "SELECT user_uuid FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return (UUID) resultSet.getObject("user_uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception
            throw e;
        }
        return null;
    }

//    @Override
//    public UUID addUUD(String username, User user) throws SQLException {
//        String sql = "select id from users where username = ?";
//        String sqlUuid = "insert into user_uuid(id, uuid) values (?,?)";
//
//        UUID uuid = UUID.randomUUID();
//
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        PreparedStatement preparedStatement1 = connection.prepareStatement(sqlUuid);
//
//        preparedStatement.setString(1, user.getUserName());
//        int id = 0;
//        ResultSet resultSet = preparedStatement.executeQuery();
//
//        while (resultSet.next()) {
//            id = resultSet.getInt("id");
//        }
//
//        preparedStatement1.setInt(1,id);
//        preparedStatement1.setObject(2,uuid);
//        preparedStatement1.executeUpdate();
//        return uuid;
//    }

//    @Override
//    public UUID addUUD(String username, User user) throws SQLException {
//        String sql = "UPDATE users SET user_uuid = ? WHERE username = ?";
//
//        UUID uuid = UUID.randomUUID();
//
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setObject(1, uuid);
//            preparedStatement.setString(2, username);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace(); // Handle or log the exception
//        }
//
//        return uuid;
//    }

}
