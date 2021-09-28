package Results;

import java.util.ArrayList;

import Models.Event;

/**
 * Plain old java class to model the result of calling EventService
 * Contains two constructors, one for success and another for errors.
 */
public class EventResult {

    ArrayList<Event> data;
    private String message;
    private final boolean success;

    //Constructor for an error
    public EventResult(String error) {
        message = error;
        success = false;
    }

    //Constructor for a successful run
    public EventResult(ArrayList<Event> eventArrayList) {
        data = eventArrayList;
        success = true;
        message = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Event> getData() {
        return data;
    }

    public void setData(ArrayList<Event> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }
}
