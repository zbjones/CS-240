package DataAccess;

import Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to manage User Database Access
 */
public class UserDao {

    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Users;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error clearing Users from database.");
        }
    }

    public void insertUser(User user) throws DataAccessException {
        String sql = "INSERT INTO Users (userName, password, email, firstName, lastName, "
        + "gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting a User into the database");
        }
    }

    public User findUser(String userName) throws DataAccessException {
        User user;
        ResultSet resultSet = null;
        String sql = "SELECT * FROM Users WHERE userName = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getString("userName"), resultSet.getString("password"),
                        resultSet.getString("email"), resultSet.getString("firstName"),
                        resultSet.getString("lastName"), resultSet.getString("gender"),
                        resultSet.getString("personID"));
                return user;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while finding a User");
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public void deleteUser(String userName) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE userName = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException();
        }
    }

}
