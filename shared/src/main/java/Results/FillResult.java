package Results;

/**
 * Plain old java class to model result of calling FilLService
 */
public class FillResult {

    private String message;
    private final boolean success;

    public FillResult(String message, boolean operationSuccess) {
        this.message = message;
        success = operationSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
