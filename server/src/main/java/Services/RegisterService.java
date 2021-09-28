package Services;

import DataAccess.*;
import FillServiceGenerators.FamilyMapData;
import FillServiceGenerators.Generator;
import Models.AuthToken;
import Models.Event;
import Models.Person;
import Models.User;
import Requests.RegisterRequest;
import Results.RegisterResult;


import java.util.ArrayList;

/**
 * Accesses the database to add a new user from the given parameters and generate ancestor data.
 */
public class RegisterService {

    private final Database database;
    private final UserDao userDao;
    private final PersonDao personDao;
    private final EventDao eventDao;
    private final AuthTokenDao authTokenDao;
    User userToAdd = new User();
    Person userAsPerson = new Person();

    public RegisterService() throws DataAccessException {
        database = new Database();
        userDao = new UserDao(database.getConnection());
        personDao = new PersonDao(database.getConnection());
        eventDao = new EventDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
    }

    public RegisterResult registerUser(RegisterRequest request) throws DataAccessException {
        try {
            //Get the new user parameters from the register request.
            getRegistrationInfo(request);

            //Check to make sure the username isn't alrady taken
            User userToCheck = userDao.findUser(userToAdd.getUsername());
            if (userToCheck != null) {
                database.closeConnection(false);
                return new RegisterResult("Error: A user with the supplied username was already found");
            }
            //Success case
            else {
                //Insert the new user, generate and insert a new authtoken
                userDao.insertUser(userToAdd);
                AuthToken authToken = new AuthToken(userToAdd.getUsername());
                authTokenDao.insertAuthToken(authToken);

                //Generate ancestor data
                Generator generator = new Generator(userAsPerson, 4);
                FamilyMapData familyMapData = generator.generateFamilyMap();
                insertFamilyMap(familyMapData);

                //Done with DAO operations, close the database.
                database.closeConnection(true);

                return new RegisterResult(authToken.getAuthToken(), userToAdd.getUsername(), userAsPerson.getPersonID());
            }
        } catch (DataAccessException exception) {
            database.closeConnection(false);
            exception.printStackTrace();
            return new RegisterResult("Internal Server Error");
        }
    }

    //Gets the data from the register request for the new user. ALso add the same parameters as the start of a person object
    public void getRegistrationInfo(RegisterRequest request) {
        userToAdd.setFirstName(request.getFirstName());
        userToAdd.setLastName(request.getLastName());
        userToAdd.setGender(request.getGender());
        userToAdd.setEmail(request.getEmail());
        userToAdd.setPassword(request.getPassword());
        userToAdd.setUsername(request.getUsername());
        userToAdd.setPersonID(userAsPerson.getPersonID());

        userAsPerson.setGender(userToAdd.getGender());
        userAsPerson.setAssociatedUsername(userToAdd.getUsername());
        userAsPerson.setFirstName(userToAdd.getFirstName());
        userAsPerson.setLastName(userToAdd.getLastName());
    }

    //Helper method to insert family map ancestor data into the database.
    private void insertFamilyMap(FamilyMapData data) throws DataAccessException {
        insertEvents(data.getEventArrayList());
        insertPersons(data.getPersonArrayList());
    }

    private void insertEvents(ArrayList<Event> events) throws DataAccessException {
        for (Event event : events) {
            eventDao.insertEvent(event);
        }
    }
    
    private void insertPersons(ArrayList<Person> persons) throws DataAccessException {
        for (Person person : persons) {
            personDao.insertPerson(person);
        }
    }
}
