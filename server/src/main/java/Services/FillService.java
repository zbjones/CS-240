package Services;

import DataAccess.*;
import FillServiceGenerators.FamilyMapData;
import FillServiceGenerators.Generator;
import Models.Event;
import Models.Person;
import Models.User;
import Results.FillResult;
import java.util.ArrayList;

/**
 * Accesses the database to add ancestor data for the specified user.
 * Deletes any person or event data already attached to the specified user.
 * Requires a nonzero number of generations.
 */
public class FillService {

    private final Database database;
    private final UserDao userDao;
    private final PersonDao personDao;
    private final EventDao eventDao;

    public FillService() throws DataAccessException {
        database = new Database();
        userDao = new UserDao(database.getConnection());
        personDao = new PersonDao(database.getConnection());
        eventDao = new EventDao(database.getConnection());
    }

    public FillResult fill(String username, int numGenerations) {
        try {
            //Check if the number of generations is valid
            if (numGenerations <= 0) {
                database.closeConnection(false);
                return new FillResult("Error: Invalid number of generations provided", false);
            }

            //Check whether the user exists in the database
            User userToCheck = userDao.findUser(username);
            if (userToCheck == null) {
                database.closeConnection(false);
                return new FillResult("Error: No user with the supplied username was found", false);
            }

            //Delete all old data attached to the user
            personDao.deleteAllPersonsAttachedToUser(username);
            eventDao.deleteAllEventsAttachedToUser(username);

            //Call the family map data generator to add some new persons and events to the database.
            Generator generator = new Generator(makePersonFromUser(userToCheck), numGenerations);
            FamilyMapData familyMapData = generator.generateFamilyMap();
            insertFamilyMap(familyMapData);

            //We are now done with DAO operations, so we close the database and commit.
            database.closeConnection(true);

            //Success, so return the success FillResult object.
            return new FillResult("Successfully added " + familyMapData.getPersonArrayList().size() + " persons and " +
                    familyMapData.getEventArrayList().size() + " events to the database.", false);
        } catch (DataAccessException exception) {
            exception.printStackTrace();
            return new FillResult("Internal Server Error", false);
        }
    }

    //Helper method to convert the user calling fill to a person object for use in the generator methods.
    private Person makePersonFromUser(User user) {
        Person personToReturn = new Person();
        personToReturn.setGender(user.getGender());
        personToReturn.setPersonID(user.getPersonID());
        personToReturn.setFirstName(user.getFirstName());
        personToReturn.setLastName(user.getLastName());
        personToReturn.setAssociatedUsername(user.getUsername());
        return personToReturn;
    }

    //Helper method to insert the familymapdata returned by the generators.
    private void insertFamilyMap(FamilyMapData data) throws DataAccessException {
        insertEvents(data.getEventArrayList());
        insertPersons(data.getPersonArrayList());
    }

    private void insertEvents(ArrayList<Event> events) throws DataAccessException {
        for (int i = 0; i < events.size(); i++) {
            eventDao.insertEvent(events.get(i));
        }
    }
    private void insertPersons(ArrayList<Person> persons) throws DataAccessException {
        for (int i = 0; i < persons.size(); i++) {
            personDao.insertPerson(persons.get(i));
        }
    }
}
