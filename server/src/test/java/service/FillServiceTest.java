package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Models.Event;
import Requests.RegisterRequest;
import Results.EventResult;
import Results.FillResult;
import Results.RegisterResult;
import Services.ClearService;
import Services.EventService;
import Services.FillService;
import Services.RegisterService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FillServiceTest {
    Database database;
    EventDao eventDao;
    RegisterService registerService;
    FillService fillService;
    String usernameToFill;
    String secondUsername;
    String authTokenToFill;

    @BeforeEach
    public void setUp() throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.clear();
        registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest("imajonesboy", "password123",
                "zmoney@mail.net", "Zac", "Jones", "m");
        RegisterResult registerResult = registerService.registerUser(registerRequest);
        usernameToFill = registerResult.getUsername();
        authTokenToFill = registerResult.getAuthtoken();

        registerService = new RegisterService();
        RegisterRequest registerRequest2 = new RegisterRequest("pdiddy", "password12345",
                "pkeller@mail.net", "Preston", "Keller", "m");
        RegisterResult registerResult2 = registerService.registerUser(registerRequest);
        secondUsername = registerResult2.getUsername();
    }

    @Test
    public void fillPass() throws DataAccessException {
        EventService eventService = new EventService();
        EventResult eventResult = eventService.findAllEvents(authTokenToFill);
        assertNull(eventResult.getMessage());
        ArrayList<Event> events = eventResult.getData();
        assertNotNull(events);
        Event eventToFind = events.get(0);
        Event finalEventToFind = eventToFind;

        fillService = new FillService();
        FillResult fillResult = fillService.fill(usernameToFill, 4);

        database = new Database();
        eventDao = new EventDao(database.getConnection());
        assertNull(eventDao.findEvent(finalEventToFind.getEventID()));
        database.closeConnection(false);

        assertNotNull(fillResult.getMessage());
        assert(fillResult.getMessage().contains("Successfully added"));

        eventService = new EventService();
        eventResult = eventService.findAllEvents(authTokenToFill);
        assertNull(eventResult.getMessage());
        events = eventResult.getData();
        assertNotNull(events);
        assert(events.size() > 0);
        eventToFind = events.get(0);
        assertNotNull(eventToFind.getAssociatedUsername());
        assertNotNull(eventToFind.getEventID());
        assertNotNull(eventToFind.getEventType());
        assertNotNull(eventToFind.getCity());
        assertNotNull(eventToFind.getCountry());
    }

    @Test
    public void fillFail() throws DataAccessException {
        String unregisteredUsername = "sethieB";

        EventService eventService = new EventService();
        EventResult eventResult = eventService.findAllEvents(authTokenToFill);
        assertNull(eventResult.getMessage());
        ArrayList<Event> events = eventResult.getData();
        assertNotNull(events);
        Event eventToFind = events.get(0);

        fillService = new FillService();
        FillResult fillResult = fillService.fill(unregisteredUsername, 4);
        assertNotNull(fillResult.getMessage());
        assert(fillResult.getMessage().contains("No user"));
        assert(fillResult.getMessage().contains("Error"));

        database = new Database();
        eventDao = new EventDao(database.getConnection());
        assertNotNull(eventDao.findEvent(eventToFind.getEventID()));
        database.closeConnection(false);
    }
}
