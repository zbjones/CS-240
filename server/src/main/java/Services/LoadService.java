package Services;

import DataAccess.*;
import Models.AuthToken;
import Models.Event;
import Models.Person;
import Models.User;
import Requests.LoadRequest;
import Results.LoadResult;

/**
 * Class to manage the loading of data into the database.
 * Contains an instance of the Database object and instances of all four DAO classes
 */
public class LoadService {

    private final Database database;
    private final AuthTokenDao authTokenDao;
    private final UserDao userDao;
    private final PersonDao personDao;
    private final EventDao eventDao;

    public LoadService() throws DataAccessException {
        database = new Database();
        authTokenDao = new AuthTokenDao(database.getConnection());
        userDao = new UserDao(database.getConnection());
        personDao = new PersonDao(database.getConnection());
        eventDao = new EventDao(database.getConnection());
    }

    //Function to manage insertion of data from the loadRequest. Returns a LoadResult
    public LoadResult loadData(LoadRequest request) throws DataAccessException {
        try {
            //Clear the database and remake the tables
            database.clearTables();
            database.createTables();

            //Insert users, persons, and events
            String userResult = insertUserArray(request.getUsers());
            if (!insertSucceeded(userResult)) {
                return new LoadResult(userResult, false);
            }

            String personResult = insertPersonArray(request.getPersons());
            if (!insertSucceeded(personResult)) {
                return new LoadResult(personResult, false);
            }

            String eventResult = insertEventArray(request.getEvents());
            if (!insertSucceeded(eventResult)) {
                return new LoadResult(eventResult, false);
            }

            //No more dao operations, so we close the the database and commit.
            database.closeConnection(true);

            //Return a successful LoadResult object
            return new LoadResult("Successfully added " + request.getUsers().length + " users, "
                    + request.getPersons().length + " persons, and "
                    + request.getEvents().length + " events to the database.", true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            database.closeConnection(false);
            return new LoadResult("There was an error accessing the database.", false);
        }
    }

    //Helper function to check whether the specific database operation succeeded
    private boolean insertSucceeded(String resultString) {
        return resultString.equals("Success.");
    }

    //Helper method to an array of Users into the database. Also manages AuthToken insertion concurrently.
    private String insertUserArray(User[] userArray) throws DataAccessException {
        try {
            if (userArray.length == 0) {
                return "No users to insert were provided!";
            }
            for (User user : userArray) {
                boolean notDuplicateUser = notDuplicateUser(user);
                if (notDuplicateUser) {
                    userDao.insertUser(user);
                    AuthToken authTokenToInsert = new AuthToken(user.getUsername());
                    authTokenDao.insertAuthToken(authTokenToInsert);
                }
                else {
                    return "One of the users being added is a duplicate.";
                }
            }
            return "Success.";
        } catch (DataAccessException exception) {
            exception.printStackTrace();
            database.closeConnection(false);
            return "There was an error accessing the database.";
        }
    }

    //Helper function to check if a particular user already exists in the database
    private boolean notDuplicateUser(User user) throws DataAccessException {
        User userToCheck = userDao.findUser(user.getUsername());
        return userToCheck == null;
    }

    //Helper method to an array of Persons into the database.
    private String insertPersonArray(Person[] personArray) throws DataAccessException {
        try {
            if (personArray.length == 0) {
                return "No persons to insert were provided!";
            }
            for (Person person : personArray) {
                boolean hasUser = personHasValidUser(person);
                boolean notDuplicatePerson = notDuplicatePerson(person);

                if (!hasUser) {
                    return "The person does not have a valid associated user.";
                }
                if (notDuplicatePerson) {
                    personDao.insertPerson(person);
                }
                else {
                    return "One of the persons being added is a duplicate.";
                }
            }
            return "Success.";
        } catch (DataAccessException exception) {
            exception.printStackTrace();
            database.closeConnection(false);
        }
        return "There was an error accessing the database.";
    }

    //Helper function to make sure each Person being added to the database has a valid user
    private boolean personHasValidUser(Person person) throws DataAccessException {
        User userToCheck = userDao.findUser(person.getAssociatedUsername());
        return userToCheck != null;
    }

    //Helper function to make sure the Person being added is not a duplicate
    private boolean notDuplicatePerson(Person person) throws DataAccessException {
        Person personToCheck = personDao.findPerson(person.getPersonID());
        return personToCheck == null;
    }

    //Helper method to an array of Events into the database.
    private String insertEventArray(Event[] eventArray) throws DataAccessException {
        try {
            if (eventArray.length == 0) {
                return "No events to insert were provided!";
            }
            for (Event event : eventArray) {
                boolean notDuplicateEvent = notDuplicateEvent(event);
                boolean hasUser = eventHasValidUser(event);
                if (!hasUser) {
                    return "The event does not have a valid associated user.";
                }
                if (notDuplicateEvent) {
                    eventDao.insertEvent(event);
                }
                else {
                    return "One of the events being added is a duplicate.";
                }
            }
            return "Success.";
        } catch(DataAccessException exception){
            exception.printStackTrace();
            database.closeConnection(false);
        }
        return "There was an error accessing the database.";
    }

    //Helper function to make sure no duplicate events are added to the database
    private boolean notDuplicateEvent(Event event) throws DataAccessException {
        Event eventToCheck = eventDao.findEvent(event.getEventID());
        return eventToCheck == null;
    }

    //Helper function to make sure the event being added is attached to a valid user in the database
    private boolean eventHasValidUser(Event event) throws DataAccessException {
        User userToCheck = userDao.findUser(event.getAssociatedUsername());
        return userToCheck != null;
    }
}
