package Results;

/**
 * Plain old java class to model result of calling LoginService
 * Contains two constructors for successful runs and runs with errors.
 */
public class LoginResult {

    private String message;
    private String username;
    private String authtoken;
    private String personID;
    boolean success;

    //Constructor for failed run
    public LoginResult(String message) {
        this.message = message;
        success = false;
        username = null;
        authtoken = null;
        personID = null;
    }

    //Constructor for successful run
    public LoginResult(String username, String authtoken, String personID) {
        success = true;
        this.username = username;
        this.authtoken = authtoken;
        this.personID = personID;
        message = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String result) {
        this.message = result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
