package dao;

import DataAccess.*;
import Models.Event;
import Models.Person;
import Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDaoTest {
    private Database db;
    private PersonDao personDao;

    private Person personOne;
    private Person personTwo;
    private Person personThree;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        personOne = new Person("NovelPersonID", "scottyB", "Scott", "Bruening",
                "M", "TheFather", "TheMother", "TheSpouse");
        personTwo = new Person("PrestonTK", "prestonThePyschMan", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");
        personThree = new Person("Aaaron", "aaredd", "Aaron", "Redd",
                "M", "Mother", "father", "Spouse");

        Connection connection = db.getConnection();

        db.clearTables();
        db.createTables();

        personDao = new PersonDao(connection);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        personDao.insertPerson(personOne);
        assertDoesNotThrow(() -> personDao.findPerson(personOne.getPersonID()));
        Person personToCheck = personDao.findPerson(personOne.getPersonID());
        assertNotNull(personToCheck);
        assertEquals(personOne, personToCheck);
    }

    @Test
    public void multipleInsertionPass() throws DataAccessException {
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);
        personDao.insertPerson(personThree);

        Person personToCheckOne = personDao.findPerson(personOne.getPersonID());
        Person personToCheckTwo = personDao.findPerson(personTwo.getPersonID());
        Person personToCheckThree = personDao.findPerson(personThree.getPersonID());

        assertNotNull(personOne);
        assertNotNull(personTwo);
        assertNotNull(personThree);

        assertEquals(personOne, personToCheckOne);
        assertEquals(personTwo, personToCheckTwo);
        assertEquals(personThree, personToCheckThree);
    }

    @Test
    public void insertionFail() throws DataAccessException {
        personTwo = new Person("NovelPersonID", "prestonThePyschMan", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");

        personDao.insertPerson(personOne);

        assertThrows(DataAccessException.class, ()-> personDao.insertPerson(personTwo));
    }

    @Test
    public void multipleInsertionFail() throws DataAccessException {
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);

        personThree = new Person("PrestonTK", "aaredd", "Aaron", "Redd",
                "M", "Mother", "father", "Spouse");

        assertThrows(DataAccessException.class, ()-> personDao.insertPerson(personThree));
    }

    @Test
    public void findPass() throws DataAccessException {
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);
        personDao.insertPerson(personThree);

        assertDoesNotThrow(() -> personDao.findPerson(personOne.getPersonID()));
        assertDoesNotThrow(() -> personDao.findPerson(personTwo.getPersonID()));
        assertDoesNotThrow(() -> personDao.findPerson(personThree.getPersonID()));

        Person personToCheckOne = personDao.findPerson(personOne.getPersonID());
        Person personToCheckTwo = personDao.findPerson(personTwo.getPersonID());
        Person personToCheckThree = personDao.findPerson(personThree.getPersonID());

        assertNotNull(personOne);
        assertNotNull(personTwo);
        assertNotNull(personThree);
    }

    @Test
    public void findFail() throws DataAccessException {
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);

        assertDoesNotThrow(() -> personDao.findPerson("Lloyd"));

        Person personToCheck = personDao.findPerson("Lloyd");
        assertNull(personToCheck);
    }

    @Test
    public void clearPass() throws DataAccessException {
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);
        personDao.insertPerson(personThree);

        personDao.clear();

        assertNull(personDao.findPerson(personTwo.getPersonID()));
    }

    @Test
    public void retrieveAllUserEventsPass() throws DataAccessException {
        personTwo = new Person("PrestonTK", "scottyB", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);
        personDao.insertPerson(personThree);

        ArrayList<Person> expectedPersonArray = new ArrayList<>();
        expectedPersonArray.add(personOne);
        expectedPersonArray.add(personTwo);

        assertDoesNotThrow(() -> personDao.getAllPersonsAttachedToUser("scottyB"));

        ArrayList<Person> resultPersonArray = personDao.getAllPersonsAttachedToUser("scottyB");

        for (int i = 0; i < expectedPersonArray.size(); i++) {
            assertEquals(expectedPersonArray.get(i), resultPersonArray.get(i));
        }
    }

    @Test
    public void retrieveAllUserEventsFail() throws DataAccessException {
        personTwo = new Person("PrestonTK", "scottyB", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);
        personDao.insertPerson(personThree);

        ArrayList<Person> expectedPersonArray = new ArrayList<>();
        expectedPersonArray.add(personOne);
        expectedPersonArray.add(personTwo);

        assertDoesNotThrow(() -> personDao.getAllPersonsAttachedToUser("scottyb"));
        ArrayList<Person> resultPersonArray = personDao.getAllPersonsAttachedToUser("scottyb");
        assertNull(resultPersonArray);
    }

    @Test
    public void deleteAllUserPersonsPass() throws DataAccessException {
        personTwo = new Person("PrestonTK", "scottyB", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");
        personDao.insertPerson(personOne);
        personDao.insertPerson(personTwo);
        personDao.insertPerson(personThree);

        assertDoesNotThrow(() -> personDao.deleteAllPersonsAttachedToUser("scottyB"));

        assertNull(personDao.findPerson(personOne.getPersonID()));
        assertNull(personDao.findPerson(personTwo.getPersonID()));
        assertNotNull(personDao.findPerson(personThree.getPersonID()));
    }
}
