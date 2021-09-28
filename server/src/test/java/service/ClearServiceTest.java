package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import DataAccess.PersonDao;
import DataAccess.UserDao;
import Models.AuthToken;
import Models.Event;
import Models.Person;
import Models.User;
import Services.ClearService;
import Results.ClearResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTest {
    private ClearResult clearResult;
    private Database database;
    private ClearService clearService;
    private PersonDao personDao;
    private UserDao userDao;
    private EventDao eventDao;
    private AuthTokenDao authTokenDao;
    User[] userArray;
    Person[] personArray;
    Event[] eventArray;

    //Initialize variables and create the tables
    @BeforeEach
    public void setUp() throws DataAccessException {
        database = new Database();
        database.openConnection();
        userDao = new UserDao(database.getConnection());
        personDao = new PersonDao(database.getConnection());
        eventDao = new EventDao(database.getConnection());
        authTokenDao = new AuthTokenDao(database.getConnection());
        database.clearTables();
        database.createTables();

        User userOne = new User("zbjones", "passwordText", "zbjones@me.com", "Zachary",
                "Jones", "M", "personIDOne");
        User userTwo = new User("zbjones1997", "mypassword", "zactheman@mymail.net", "Zmoney",
                "Jonesy", "M", "personIDTwo");
        User userThree = new User("SethieB", "BrownDog98", "sethlovessleds@mymail.net", "Seth",
                "Brown", "M", "personIDThree");
        userArray = new User[]{ userOne, userTwo, userThree };

        Person personOne = new Person("personIDOne", "zbjones", "Scott", "Bruening",
                "M", "TheFather", "TheMother", "TheSpouse");
        Person personTwo = new Person("personIDTwo", "zbjones1997", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");
        Person personThree = new Person("personIDThree", "SethieB", "Aaron", "Redd",
                "M", "Mother", "father", "Spouse");
        personArray = new Person[]{ personOne, personTwo, personThree};

        Event eventOne = new Event("Biking_123A", "zbjones", "personIDOne",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        Event eventTwo = new Event("Getting attacked by a bear", "zbjones1997", "personIDTwo",
                44.3f, 110.1f, "Congo", "Congo City",
                "Biking_Around", 2016);
        Event eventThree = new Event("Car accident", "SethieB", "personIDThree",
                44.3f, 110.1f, "Australia", "Brisbane",
                "Disaster", 2021);
        eventArray = new Event[]{ eventOne, eventTwo, eventThree };

        for (int i = 0; i < 3; i++) {
            userDao.insertUser(userArray[i]);
            personDao.insertPerson(personArray[i]);
            eventDao.insertEvent(eventArray[i]);
            authTokenDao.insertAuthToken(new AuthToken(userArray[i].getUsername()));
        }
        database.closeConnection(true);
    }

    //Verify that clearing the database removes information from the tables
    @Test
    public void clearPass() throws DataAccessException {
        database = new Database();
        userDao = new UserDao(database.getConnection());
        assertNotNull(userDao.findUser("zbjones"));

        clearService = new ClearService();
        ClearResult clearResult = clearService.clear();
        assertEquals("Clear succeeded.", clearResult.getMessage());

        userDao = new UserDao(database.getConnection());
        assertNull(userDao.findUser("zbjones"));
        database.closeConnection(false);
    }

    //Test the success of clearing an empty database (two successive clears)
    @Test
    public void secondClearPass() throws DataAccessException {
        clearService = new ClearService();
        clearResult = clearService.clear();
        assertEquals("Clear succeeded.", clearResult.getMessage());

        database = new Database();
        userDao = new UserDao(database.getConnection());
        assertNull(userDao.findUser("zbjones"));
        database.closeConnection(false);

        clearService = new ClearService();
        ClearResult secondResult = clearService.clear();
        assertEquals("Clear succeeded.", secondResult.getMessage());
    }

}
