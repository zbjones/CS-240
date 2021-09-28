package Results;

import java.util.ArrayList;

import Models.Person;

/**
 * Plain old java class to model the result of calling PersonService
 * Contains two constructors, one for success and another for errors.
 */
public class PersonResult {

    private String message;
    private boolean success;
    ArrayList<Person> data;

    //Constructor for errors
    public PersonResult(String message) {
        this.message = message;
        success = false;
    }

    //Constructor for successful runs
    public PersonResult(ArrayList<Person> personArray) {
        message = null;
        success = true;
        data = personArray;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Person> getData() {
        return data;
    }

    public void setData(ArrayList<Person> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
