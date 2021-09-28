package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.PersonDao;
import Models.AuthToken;
import Models.Person;
import Results.PersonIDResult;


/**
 * Accesses the database to find a specific person.
 * The person must belong to the user associated with the provided authtoken.
 */
public class PersonIDService {

    private final Database database;
    private final PersonDao personDao;
    private final AuthTokenDao authTokenDao;

    public PersonIDService() throws DataAccessException {
        database = new Database();
        personDao = new PersonDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
    }

    public PersonIDResult findSinglePerson(String personID, String authToken) throws DataAccessException {
        try {
            //Check to make sure the authtoken is valid
            AuthToken authTokenToCheck = authTokenDao.findAuthToken(authToken);
            if (authTokenToCheck == null) {
                database.closeConnection(false);
                return new PersonIDResult("Error: Invalid Authorization Token");
            }
            //Check to make sure the person to return is valid and is associated with the current user.
            Person personToFind = personDao.findPerson(personID);
            if (personToFind == null) {
                database.closeConnection(false);
                return new PersonIDResult("Error: Invalid personID parameter");
            }
            else if (!authTokenAndPersonMatch(authTokenToCheck, personToFind)) {
                database.closeConnection(false);
                return new PersonIDResult("Error: Requested person does not belong to this user.");
            }

            //Success case, close the database and commit, return the successful object.
            else {
                database.closeConnection(true);
                return new PersonIDResult(personToFind.getPersonID(), personToFind.getAssociatedUsername(),
                        personToFind.getFirstName(), personToFind.getLastName(), personToFind.getGender(),
                        personToFind.getFatherID(), personToFind.getMotherID(), personToFind.getSpouseID());
            }
        } catch (DataAccessException exception) {
            database.closeConnection(false);
            exception.printStackTrace();
            return new PersonIDResult("Internal Server Error");
        }
    }

    //Make sure the user associated with the authtoken and the specified person are the same
    private boolean authTokenAndPersonMatch(AuthToken authToken, Person person) {
        return person.getAssociatedUsername().equals(authToken.getUsername());
    }
}
