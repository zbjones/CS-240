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
import Requests.LoadRequest;
import Requests.LoginRequest;
import Results.EventIDResult;
import Results.LoadResult;
import Results.LoginResult;
import Services.EventIDService;
import Services.LoadService;
import Services.LoginService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EventIDServiceTest {
    private Database database;
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
    public void eventIDPass() throws DataAccessException {
        EventIDService eventIDService = new EventIDService();
        EventIDResult eventIDResult= eventIDService.findSingleEvent("Betty_Death", sheilaAuthToken);
        assertNotNull(eventIDResult);
        assertEquals("Birmingham", eventIDResult.getCity());

        eventIDService = new EventIDService();
        eventIDResult = eventIDService.findSingleEvent("Mrs_Rodham_Java", sheilaAuthToken);
        assertNotNull(eventIDResult);
        assertEquals(1890, eventIDResult.getYear());
    }

    @Test
    public void eventIDFail() throws DataAccessException {
        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest("patrick", "spencer");
        LoginResult loginResult = loginService.login(loginRequest);
        String patrickAuthToken = loginResult.getAuthtoken();

        EventIDService eventIDService = new EventIDService();
        EventIDResult eventIDResult= eventIDService.findSingleEvent("Betty_Death", patrickAuthToken);
        assertNotNull(eventIDResult);
        assertEquals("Error: Requested event does not belong to this user.", eventIDResult.getMessage());
    }
}

