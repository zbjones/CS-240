package Services;


import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Models.AuthToken;
import Models.Event;
import Results.EventIDResult;

/**
 * EventIDService accesses the database and returns a single event requested by the EventID.
 * Checks to make sure the authtoken is valid, the event exists, and the user referenced by the authtoken and event are the same
 */
public class EventIDService {

    private final Database database;
    private final EventDao eventDao;
    private final AuthTokenDao authTokenDao;

    public EventIDService() throws DataAccessException {
        database = new Database();
        eventDao = new EventDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
    }

    public EventIDResult findSingleEvent(String eventID, String authToken) throws DataAccessException {
        try {
            AuthToken authTokenToCheck = authTokenDao.findAuthToken(authToken);
            Event eventToFind = eventDao.findEvent(eventID);

            //No more database operations, so we close the database here
            database.closeConnection(false);

            //Check for various errors in returning the specific event
            if (authTokenToCheck == null) {
                return new EventIDResult("Error: Invalid Authorization Token");
            }
            else if (eventToFind == null) {
                return new EventIDResult("Error: Invalid eventID parameter");
            }
            else if (!authTokenAndEventMatch(authTokenToCheck, eventToFind)) {
                return new EventIDResult("Error: Requested event does not belong to this user.");
            }

            //Success case, pass in all of the required parameters from the event object successfully found above
            else {
                return new EventIDResult(eventToFind.getEventID(), eventToFind.getAssociatedUsername(),
                        eventToFind.getPersonID(), eventToFind.getLatitude(), eventToFind.getLongitude(),
                        eventToFind.getCountry(), eventToFind.getCity(),
                        eventToFind.getEventType(), eventToFind.getYear());
            }
        } catch (DataAccessException exception) {
            exception.printStackTrace();
            return new EventIDResult("Internal Server Error");
        }
    }

    //Helper method to verify the username associated with the authtoken and event are the same
    private boolean authTokenAndEventMatch(AuthToken authToken, Event event) {
        return event.getAssociatedUsername().equals(authToken.getUsername());
    }
}
