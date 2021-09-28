package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Models.AuthToken;
import Models.Event;
import Results.EventResult;

import java.util.ArrayList;

/**
 * EventService accesses the database and returns all the events associated with a particular user.
 * Checks to make sure the authorization token provided is valid.
 */
public class EventService {

    private final Database database;
    private final EventDao eventDao;
    private final AuthTokenDao authTokenDao;

    public EventService() throws DataAccessException {
        database = new Database();
        eventDao = new EventDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
    }

    public EventResult findAllEvents(String authToken) throws DataAccessException {
        try {
            //Make sure the provided authtoken exists and is valid.
            AuthToken authTokenToCheck = authTokenDao.findAuthToken(authToken);
            if (authTokenToCheck == null) {
                return new EventResult("Error: Invalid Authorization Token");
            }
            ArrayList<Event> eventArray = eventDao.getAllEventsAttachedToUser(authTokenToCheck.getUsername());

            //No more database operations, so we close the database
            database.closeConnection(false);

            //If we didn't get any events to populate the arraylist, we need to return an error saying there are no current users
            if (eventArray.isEmpty()) {
                return new EventResult("Error: There are no people attached to the current user.");
            }
            //Success case
            else {
                return new EventResult(eventArray);
            }
        } catch (DataAccessException exception) {
            exception.printStackTrace();
            return new EventResult("Internal Server Error");
        }
    }
}
