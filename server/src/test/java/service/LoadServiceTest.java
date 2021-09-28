package service;

import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Models.Event;
import Models.Person;
import Models.User;
import Requests.LoadRequest;
import Results.LoadResult;
import Services.LoadService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoadServiceTest {
    private LoadRequest loadRequest;
    private LoadResult loadResult;
    private Database database;
    private LoadService loadService;
    User[] userArrayFail;
    User[] userArray;
    Person[] personArray;
    Event[] eventArray;

    @BeforeEach
    public void setUp() throws DataAccessException {
        database = new Database();
        database.openConnection();
        database.clearTables();
        database.createTables();
        database.closeConnection(true);

        User userOne = new User("zbjones", "passwordText", "zbjones@me.com", "Zachary",
                "Jones", "M", "personIDOne");
        User userTwo = new User("zbjones1997", "mypassword", "zactheman@mymail.net", "Zmoney",
                "Jonesy", "M", "personIDTwo");
        User userThree = new User("SethieB", "BrownDog98", "sethlovessleds@mymail.net", "Seth",
                "Brown", "M", "personIDThree");
        User userFour = new User(null, "password", null, "Scotty", "Bee", "M", null);
        userArray = new User[]{ userOne, userTwo, userThree };
        userArrayFail = new User[]{ userOne, userTwo, userFour };

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
    }


    @Test
    public void loadPass() throws DataAccessException {
        loadService = new LoadService();
        loadRequest = new LoadRequest(userArray, personArray, eventArray);
        loadResult = loadService.loadData(loadRequest);
        assertEquals("Successfully added 3 users, 3 persons, and 3 events to the database.", loadResult.getMessage());
    }

    @Test
    public void loadPassWithFile() throws IOException, DataAccessException {
        loadService = new LoadService();
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer" +
                "\\passoffFiles\\LoadData.json"));
        loadRequest = gson.fromJson(reader, LoadRequest.class);
        loadResult = loadService.loadData(loadRequest);

        database = new Database();
        UserDao userDao = new UserDao(database.getConnection());
        assertNotNull(userDao.findUser("sheila"));
        database.closeConnection(true);

        assertEquals("Successfully added 2 users, 11 persons, and 19 events to the database.",
                loadResult.getMessage());
    }

    @Test
    public void loadFail() throws DataAccessException {
        loadService = new LoadService();
        loadRequest = new LoadRequest(userArrayFail, personArray, eventArray);
        loadResult = loadService.loadData(loadRequest);
        assertEquals("There was an error accessing the database.", loadResult.getMessage());
    }
}

