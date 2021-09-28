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
import Services.LoadService;
import Services.LoginService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginServiceTest {

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


    }

    @Test
    public void loginPass() throws DataAccessException {
        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        LoginResult loginResult = loginService.login(loginRequest);

        assertEquals("Sheila_Parker", loginResult.getPersonID());

        LoginService loginService2 = new LoginService();
        LoginRequest loginRequest2 = new LoginRequest("patrick", "spencer");
        LoginResult loginResult2 = loginService2.login(loginRequest2);

        assertEquals("Patrick_Spencer", loginResult2.getPersonID());
    }

    @Test
    public void loginFail() throws DataAccessException {
        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest("sheila", "packer");
        LoginResult loginResult = loginService.login(loginRequest);

        assertEquals("Error: The provided password is incorrect.", loginResult.getMessage());

        LoginService loginService2 = new LoginService();
        LoginRequest loginRequest2 = new LoginRequest("shelly", "parker");
        LoginResult loginResult2 = loginService2.login(loginRequest2);

        assertEquals("Error: The user to login was not found.", loginResult2.getMessage());
    }
}
