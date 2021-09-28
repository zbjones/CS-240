package service;

import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import DataAccess.DataAccessException;
import Requests.LoadRequest;
import Requests.LoginRequest;
import Results.LoadResult;
import Results.LoginResult;
import Results.PersonIDResult;
import Services.LoadService;
import Services.LoginService;
import Services.PersonIDService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PersonIDServiceTest {
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
    public void personIDPass() throws DataAccessException {
        PersonIDService personIDService = new PersonIDService();
        PersonIDResult personIDResult = personIDService.findSinglePerson("Mrs_Jones", sheilaAuthToken);
        assertNotNull(personIDResult);
        assertEquals("Frank_Jones", personIDResult.getSpouseID());

        personIDService = new PersonIDService();
        personIDResult = personIDService.findSinglePerson("Blaine_McGary", sheilaAuthToken);
        assertNotNull(personIDResult);
        assertEquals("Ken_Rodham", personIDResult.getFatherID());
    }

    @Test
    public void personIDFail() throws DataAccessException {
        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest("patrick", "spencer");
        LoginResult loginResult = loginService.login(loginRequest);
        String patrickAuthToken = loginResult.getAuthtoken();

        PersonIDService personIDService = new PersonIDService();
        PersonIDResult personIDResult = personIDService.findSinglePerson("Mrs_Jones", patrickAuthToken);
        assertNotNull(personIDResult);
        assertEquals("Error: Requested person does not belong to this user.", personIDResult.getMessage());
    }
}

