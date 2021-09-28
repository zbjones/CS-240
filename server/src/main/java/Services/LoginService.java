package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Models.AuthToken;
import Models.User;
import Requests.LoginRequest;
import Results.LoginResult;


/**
 * Verifies the user exists in the database (checks username) and ensures the password is correct.
 * Returns a new authtoken upon success.
 */
public class LoginService {
    private final Database database;
    private final AuthTokenDao authTokenDao;
    private final UserDao userDao;
    private AuthToken authToken;
    private User userToLogin;

    public LoginService() throws DataAccessException {
        database = new Database();
        userDao = new UserDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        try {
            //Check whether the user specified exists.
            userToLogin = userDao.findUser(request.getUsername());
            if (userToLogin == null) {
                database.closeConnection(false);
                return new LoginResult("Error: The user to login was not found.");
            }
            //Check if the password is correct
            else if (!passwordIsValid(userToLogin, request.getPassword())) {
                database.closeConnection(false);
                return new LoginResult("Error: The provided password is incorrect.");
            }
            //Success case, get a new authtoken, add it to the database, and return it with the result object
            else {
                authToken = new AuthToken(request.getUsername());
                authTokenDao.insertAuthToken(authToken);
                database.closeConnection(true);
                return new LoginResult(userToLogin.getUsername(), authToken.getAuthToken(), userToLogin.getPersonID());
            }
        } catch(DataAccessException exception) {
            database.closeConnection(false);
            return new LoginResult("Error: There was an database access error while logging in a user.");
        }
    }

    //Helper method to check if the provided password matches the password associated with the given username.
    public boolean passwordIsValid(User user, String password) throws DataAccessException {
        String expectedPassword = user.getPassword();
        return expectedPassword.equals(password);
    }
}
