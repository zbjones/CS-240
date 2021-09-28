package DataAccess;

import Models.AuthToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to manage AuthToken Database Access.
 * Handles insertion, deletion, and  authtoken retrieval
 */
public class AuthTokenDao {

    private final Connection connection;

    public AuthTokenDao(Connection connection) {
        this.connection = connection;
    }

    public void insertAuthToken(AuthToken token) throws DataAccessException {
        String sql = "INSERT INTO AuthToken (token, username) VALUES(?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, token.getAuthToken());
            preparedStatement.setString(2, token.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error encountered while inserting an auth token into the database.");
        }
    }

    public AuthToken findAuthToken(String authTokenName) throws DataAccessException {
        AuthToken authToken = null;
        String sql = "SELECT * FROM AuthToken WHERE token = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, authTokenName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                authToken = new AuthToken(resultSet.getString("token"),
                        resultSet.getString("userName"));
                return authToken;
            }
        }
        catch (SQLException exception){
            throw new DataAccessException("Error encountered while finding an AuthToken");
        }
        return null;
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM AuthToken;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error encountered clearing the authTokens from the table.");
        }
    }
}
