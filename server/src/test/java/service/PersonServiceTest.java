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
import Models.Person;
import Requests.LoadRequest;
import Requests.LoginRequest;
import Results.LoadResult;
import Results.LoginResult;
import Results.PersonResult;
import Services.LoadService;
import Services.LoginService;
import Services.PersonService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PersonServiceTest {
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
    public void personPass() throws DataAccessException {
        PersonService personService = new PersonService();
        PersonResult personResult = personService.findAllPersons(sheilaAuthToken);
        assertNotNull(personResult);
        ArrayList<Person> personArrayList = personResult.getData();
        assert(personArrayList.size() == 8);
        Person expectedFirstPerson = new Person("Sheila_Parker", "sheila", "Sheila",
                "Parker", "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");
        assertEquals(expectedFirstPerson, personArrayList.get(0));
    }

    @Test
    public void personFail() throws DataAccessException {
        sheilaAuthToken = sheilaAuthToken + "extraText";
        PersonService personService = new PersonService();
        PersonResult personResult = personService.findAllPersons(sheilaAuthToken);
        assertNotNull(personResult);
        assertEquals("Error: Invalid Authorization Token", personResult.getMessage());
    }
}
