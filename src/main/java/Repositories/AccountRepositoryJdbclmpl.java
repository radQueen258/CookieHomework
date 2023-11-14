package Repositories;

import Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRepositoryJdbclmpl implements AccountRepository{

    private final Connection connection;
    private static final String SQL_INSERT_USER = "insert into users (username, email, password) values (?,?,?) ";

    public AccountRepositoryJdbclmpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User user) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_USER);
        preparedStatement.setString(1, user.getUserName());
        preparedStatement.setString(2, user.getUserEmail());
        preparedStatement.setString(3, user.getUserPassword());

        preparedStatement.executeUpdate();
        System.out.println("Executed");

    }

    @Override
    public boolean login(String username, String password, User user) throws SQLException {
        String sql = "select * from users where username = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getUserName());
        ResultSet resultSet = preparedStatement.executeQuery();

        String accountUsername = "";
        String accountPassword = "";

        while (resultSet.next()) {
            accountUsername = resultSet.getString("username");
            accountPassword = resultSet.getString("password");
        }

        if (accountUsername.equals(username) && accountPassword.equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean findUUID(UUID uuid) throws SQLException {
        String sql = "select count(*) from users_uuid where UUID = ?";
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
    public UUID addUUD(String username, User user) throws SQLException {
        String sqlUser = "SELECT users_id FROM users WHERE username = ?";
        String insertSqlUuid = "INSERT INTO users_uuid(id, uuid) VALUES(?, ?)";

        UUID uuid = UUID.randomUUID();

        PreparedStatement preparedStatement1 = connection.prepareStatement(sqlUser);
        PreparedStatement preparedStatement2 = connection.prepareStatement(insertSqlUuid);

        preparedStatement1.setString(1, user.getUserName());
        int users_id = 0;
        ResultSet resultSet = preparedStatement1.executeQuery();

//        while (resultSet.next()){
//            users_id = resultSet.getInt("users_id");
//        }

        if (resultSet.next()) {
            users_id = resultSet.getInt("users_id");
        } else {
            throw new SQLException("User not found");
        }

        preparedStatement2.setInt(1,users_id);
        preparedStatement2.setObject(2, uuid);
        preparedStatement2.executeUpdate();

        return uuid;
    }



}
