package dao;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Models.Event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insertEvent method is working and failing in the right ways
public class EventDaoTest {
    private Database db;
    private Event bestEvent;
    private Event eventTwo;
    private Event eventThree;
    private EventDao eventDao;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new event with random data
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        eventTwo = new Event("Getting attacked by a bear", "Gale", "Z Brian Jones",
                44.3f, 110.1f, "Congo", "Congo City",
                "Biking_Around", 2016);
         eventThree = new Event("Car accident", "Galileo", "Unfortunate Stranger",
                44.3f, 110.1f, "Australia", "Brisbane",
                "Disaster", 2021);
        //Here, we'll open the connection in preparation for the test case to use it
        Connection connection = db.getConnection();
        //Let's clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Make the tables again
        db.createTables();
        //Then we pass that connection to the EventDAO so it can access the database
        eventDao = new EventDao(connection);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        //Here we close the connection to the database file so it can be opened elsewhere.
        //We will leave commit to false because we have no need to save the changes to the database
        //between test cases
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        //While insertEvent returns a bool we can't use that to verify that our function actually worked
        //only that it ran without causing an error
        eventDao.insertEvent(bestEvent);
        //So lets use a findEvent method to get the event that we just put in back out
        Event compareTest = eventDao.findEvent(bestEvent.getEventID());
        //First lets see if our findEvent found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insertEvent did put something in, and that it didn't change the
        //data in any way
        assertEquals(bestEvent, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        //lets do this test again but this time lets try to make it fail
        //if we call the method the first time it will insertEvent it successfully
        eventDao.insertEvent(bestEvent);
        //but our sql table is set up so that "eventID" must be unique. So trying to insertEvent it
        //again will cause the method to throw an exception
        //Note: This call uses a lambda function. What a lambda function is is beyond the scope
        //of this class. All you need to know is that this line of code runs the code that
        //comes after the "()->" and expects it to throw an instance of the class in the first parameter.
        assertThrows(DataAccessException.class, ()-> eventDao.insertEvent(bestEvent));
    }

    @Test
    public void multipleInsertionPass() throws DataAccessException {
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        eventTwo = new Event("Getting attacked by a bear", "zbjones", "Z Brian Jones",
                44.3f, 110.1f, "Congo", "Congo City",
                "Biking_Around", 2016);
        eventDao.insertEvent(bestEvent);
        eventDao.insertEvent(eventTwo);

        Event eventOneToCheck = eventDao.findEvent(bestEvent.getEventID());
        Event eventTwoToCheck = eventDao.findEvent(eventTwo.getEventID());

        assertNotNull(eventOneToCheck);
        assertNotNull(eventTwoToCheck);

        assertEquals(bestEvent, eventOneToCheck);
        assertEquals(eventTwo, eventTwoToCheck);
    }

    @Test
    public void multipleInsertionFail() throws DataAccessException {
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        eventTwo = new Event("Biking_123A", "zbjones", "Z Brian Jones",
                44.3f, 110.1f, "Congo", "Congo City",
                "Biking_Around", 2016);
        eventDao.insertEvent(bestEvent);
        assertThrows(DataAccessException.class, ()-> eventDao.insertEvent(eventTwo));
    }

    @Test
    void findPass() throws DataAccessException {
        eventDao.insertEvent(bestEvent);

        assertDoesNotThrow(() ->  eventDao.findEvent("Biking_123A"));
        Event eventToCheck = eventDao.findEvent("Biking_123A");
        assertNotNull(eventToCheck);
    }

    @Test
    void findFail() throws DataAccessException {
        eventDao.insertEvent(eventTwo);

        assertDoesNotThrow(() ->  eventDao.findEvent("Biking_123A"));
        Event eventToCheck = eventDao.findEvent("Biking_123A");
        assertNull(eventToCheck);
    }

    @Test
    void retrieveAllUserEventsPass() throws DataAccessException {
        eventDao.insertEvent(bestEvent);
        eventDao.insertEvent(eventTwo);
        eventDao.insertEvent(eventThree);

        ArrayList<Event> expectedEventsArray = new ArrayList<>();
        expectedEventsArray.add(bestEvent);
        expectedEventsArray.add(eventTwo);

        ArrayList<Event> foundEventsArray = null;
        foundEventsArray = eventDao.getAllEventsAttachedToUser("Gale");
        assertNotNull(foundEventsArray);
        assertEquals(expectedEventsArray, foundEventsArray);
    }

    @Test
    void retrieveAllUserEventsFail() throws DataAccessException {
        eventDao.insertEvent(bestEvent);
        eventDao.insertEvent(eventTwo);
        eventDao.insertEvent(eventThree);

        ArrayList<Event> foundEventsArray = new ArrayList<Event>();
        foundEventsArray = eventDao.getAllEventsAttachedToUser("Gale");

        for (Event event : foundEventsArray) {
            String currentUserName = event.getAssociatedUsername();
            assertNotEquals(currentUserName, "Galileo");
        }
    }

    @Test
    void clearTest() throws DataAccessException {
        eventDao.insertEvent(bestEvent);
        eventDao.insertEvent(eventTwo);
        eventDao.insertEvent(eventThree);

        eventDao.clear();
        assertNull(eventDao.findEvent(bestEvent.getEventID()));
    }

    @Test
    void deleteAllUserEventsPass() throws DataAccessException {
        eventDao.insertEvent(bestEvent);
        eventDao.insertEvent(eventTwo);
        eventDao.insertEvent(eventThree);

        eventDao.deleteAllEventsAttachedToUser("Gale");

        assertNull(eventDao.findEvent(bestEvent.getEventID()));
        assertNull(eventDao.findEvent(eventTwo.getEventID()));
        assertNotNull(eventDao.findEvent(eventThree.getEventID()));

        eventDao.deleteAllEventsAttachedToUser("Galileo");
        assertNull(eventDao.findEvent(eventThree.getEventID()));
    }

    @Test
    void deleteAllUserEventsFail() throws DataAccessException {
        eventDao.insertEvent(bestEvent);
        eventDao.insertEvent(eventTwo);
        eventDao.insertEvent(eventThree);

        eventDao.deleteAllEventsAttachedToUser("Galle");

        Event eventToCheck = eventDao.findEvent(bestEvent.getEventID());
        assertNotNull(eventToCheck);
    }

}
