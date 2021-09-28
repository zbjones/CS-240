package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import DataAccess.UserDao;
import Models.AuthToken;
import Models.Person;
import Models.User;
import Requests.RegisterRequest;
import Results.PersonResult;
import Results.RegisterResult;
import Services.ClearService;
import Services.PersonService;
import Services.RegisterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegisterServiceTest {
    RegisterService registerService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.clear();
        registerService = new RegisterService();
    }

    @Test
    public void RegisterPass() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("imajonesboy", "password123",
                "zmoney@mail.net", "Zac", "Jones", "m");
        RegisterResult registerResult = registerService.registerUser(registerRequest);
        assertNull(registerResult.getMessage());
        assert(registerResult.isSuccess());
        String username = registerResult.getUsername();
        String authtoken = registerResult.getAuthtoken();
        String personID = registerResult.getPersonID();

        Database database = new Database();
        AuthTokenDao authTokenDao = new AuthTokenDao(database.getConnection());
        AuthToken authToken = authTokenDao.findAuthToken(authtoken);
        assertEquals(username, authToken.getUsername());

        UserDao userDao = new UserDao(database.getConnection());
        User user = userDao.findUser(username);
        assertEquals(personID, user.getPersonID());

        PersonService personService = new PersonService();
        PersonResult personResult = personService.findAllPersons(authtoken);
        assertNotNull(personResult);
        assertNull(personResult.getMessage());
        ArrayList<Person> persons = personResult.getData();
        assert(persons.size() > 10);

        Person person = persons.get(0);
        assertNotNull(person.getPersonID());
        assertNotNull(person.getAssociatedUsername());
        assertNotNull(person.getGender());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
    }

    @Test
    public void registerFail() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("imajonesboy", "password123",
                "zmoney@mail.net", "Zac", "Jones", "m");
        RegisterResult registerResult = registerService.registerUser(registerRequest);
        assertNull(registerResult.getMessage());
        assert(registerResult.isSuccess());

        registerService = new RegisterService();
        RegisterRequest registerRequest2 = new RegisterRequest("imajonesboy", "password12345",
                "pkeller@mail.net", "Preston", "Keller", "m");
        RegisterResult registerResult2 = registerService.registerUser(registerRequest);
        assertNotNull(registerResult2);
        assertNotNull(registerResult2.getMessage());
        assert(registerResult2.getMessage().contains("username was already found"));

        Database database = new Database();
        EventDao eventDao = new EventDao(database.getConnection());
        assertNotNull(eventDao.getAllEventsAttachedToUser("imajonesboy"));
        database.closeConnection(false);
    }
}
