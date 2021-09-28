package Results;

/**
 * Plain old java class to model the result of calling RegisterService
 * Contains two constructors, one for success and another for errors.
 */
public class RegisterResult {

    private String message;
    private boolean success;
    private String authtoken;
    private String username;
    private String personID;

    //Constructor for errors
    public RegisterResult(String message) {
        this.message = message;
        success = false;
    }

    //Constructor for successful runs
    public RegisterResult(String authtoken, String username, String personID) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        success = true;
        message = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
