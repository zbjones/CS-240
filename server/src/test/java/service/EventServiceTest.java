package service;

import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import DataAccess.DataAccessException;
import Models.Event;
import Requests.LoadRequest;
import Requests.LoginRequest;
import Results.EventResult;
import Results.LoadResult;
import Results.LoginResult;
import Services.EventService;
import Services.LoadService;
import Services.LoginService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EventServiceTest {
    private String sheilaAuthToken;

    @BeforeEach
    public void setUp() throws DataAccessException, IOException {
        LoadService loadService = new LoadService();
        LoadRequest loadRequest;
        LoadResult loadResult;
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer" +
                "\\passoffFiles\\LoadData.json"));
        loadRequest = gson.fromJson(reader, LoadRequest.class);
        loadResult = loadService.loadData(loadRequest);

        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        LoginResult loginResult = loginService.login(loginRequest);
        sheilaAuthToken = loginResult.getAuthtoken();
    }

    @Test
    public void eventPass() throws DataAccessException {
        EventService eventService = new EventService();
        EventResult eventResult= eventService.findAllEvents(sheilaAuthToken);
        assertNotNull(eventResult);
        ArrayList<Event> eventArrayList = eventResult.getData();
        assert(eventArrayList.size() == 16);
        Event expectedFirstEvent = new Event("Sheila_Birth", "sheila", "Sheila_Parker",
                (float) -36.1833, (float) 144.9667, "Australia", "Melbourne", "birth", 1970);
        assertEquals(expectedFirstEvent, eventArrayList.get(0));
    }

    @Test
    public void eventFail() throws DataAccessException {
        sheilaAuthToken = sheilaAuthToken + "extraText";
        EventService eventService = new EventService();
        EventResult eventResult= eventService.findAllEvents(sheilaAuthToken);
        assertNotNull(eventResult);
        assertEquals("Error: Invalid Authorization Token", eventResult.getMessage());
    }
}
