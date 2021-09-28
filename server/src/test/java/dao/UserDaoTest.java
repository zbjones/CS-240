package dao;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    private Database db;
    private User userOne;
    private User userTwo;
    private User userThree;
    private UserDao userDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        userOne = new User("zbjones", "passwordText", "zbjones@me.com", "Zachary",
                "Jones", "M", "a#zzzz2349");
        userTwo = new User("zbjones1997", "mypassword", "zactheman@mymail.net", "Zmoney",
                "Jonesy", "M", "a#z34s3j2349");
        userThree = new User("SethieB", "BrownDog98", "sethlovessleds@mymail.net", "Seth",
                "Brown", "M", "a#234442119854789");


        Connection conn = db.getConnection();

        db.clearTables();
        db.createTables();

        userDao = new UserDao(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertTestPass() throws DataAccessException {
        userDao.insertUser(userOne);

        User userToCompare = null;
        userToCompare = userDao.findUser(userOne.getUsername());
        assertNotNull(userToCompare);
        assertEquals(userOne, userToCompare);
    }

    @Test
    public void insertTestFail() throws DataAccessException {
        userDao.insertUser(userOne);

        userTwo = new User("zbjones", "mypassword", "zactheman@mymail.net", "Zmoney",
                "Jonesy", "M", "a#z34s3j2349");

        assertThrows(DataAccessException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDao.insertUser(userTwo);
            }
        });
    }

    @Test
    public void findPass() throws DataAccessException {
        userDao.insertUser(userOne);
        userDao.insertUser(userTwo);

        User userToCheck = userDao.findUser(userOne.getUsername());
        assertNotNull(userToCheck);
        assertEquals(userOne, userToCheck);
    }

    @Test
    public void findFail() throws DataAccessException {
        userDao.insertUser(userThree);
        userDao.insertUser(userOne);

        User userToCheck = userDao.findUser(userTwo.getUsername());
        assertNull(userToCheck);
    }

    @Test
    void clearTest() throws DataAccessException {
        userDao.insertUser(userOne);
        userDao.insertUser(userTwo);
        userDao.insertUser(userThree);
        userDao.clear();
        assertNull(userDao.findUser(userOne.getUsername()));
        assertNull(userDao.findUser(userTwo.getUsername()));
        assertNull(userDao.findUser(userThree.getUsername()));
    }

    @Test
    void deleteUser() throws DataAccessException {
        userDao.insertUser(userOne);
        userDao.insertUser(userTwo);
        userDao.insertUser(userThree);

        userDao.deleteUser(userOne.getUsername());
        assertNull(userDao.findUser(userOne.getUsername()));
        assertNotNull(userDao.findUser(userTwo.getUsername()));
    }


}
