package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.PersonDao;
import Models.AuthToken;
import Models.Person;
import Results.PersonResult;


import java.util.ArrayList;

/**
 * Returns all the persons associated with the provided authtoken's user
 */
public class PersonService {
    private final Database database;
    private final PersonDao personDao;
    private final AuthTokenDao authTokenDao;

    public PersonService() throws DataAccessException {
        database = new Database();
        personDao = new PersonDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
    }

    public PersonResult findAllPersons(String authToken) throws DataAccessException {
        try {
            //Check to make sure the authtoken is valid.
            AuthToken authTokenToCheck = authTokenDao.findAuthToken(authToken);
            if (authTokenToCheck == null) {
                return new PersonResult("Error: Invalid Authorization Token");
            }

            //Save all the persons associated with the authtoken's user. Close the database (done with DAO operations)
            ArrayList<Person> personArrayList = personDao.getAllPersonsAttachedToUser(authTokenToCheck.getUsername());
            database.closeConnection(false);

            //Error case if there are no persons associated with the user. ELSE success case.
            if (personArrayList.isEmpty()) {
                return new PersonResult("Error: There are no people attached to the current user.");
            }
            else {
                return new PersonResult(personArrayList);
            }
        } catch (DataAccessException exception) {
            exception.printStackTrace();
            return new PersonResult("Internal Server Error");
        }
    }
}