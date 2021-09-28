package dao;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import Models.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDaoTest {

    private Database db;
    private AuthToken authTokenOne;
    private AuthToken authTokenTwo;
    private AuthTokenDao authTokenDao;


    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        authTokenOne = new AuthToken("asjdflkj23455221", "theUser");
        authTokenTwo = new AuthToken("qwertyuiop112345", "theMachine");

        Connection connection = db.getConnection();
        db.clearTables();
        db.createTables();

        authTokenDao = new AuthTokenDao(connection);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        authTokenDao.insertAuthToken(authTokenOne);

        AuthToken authTokenToCompare = null;
        authTokenToCompare = authTokenDao.findAuthToken(authTokenOne.getAuthToken());
        assertNotNull(authTokenToCompare);
        assertEquals(authTokenOne, authTokenToCompare);
    }

    @Test
    public void insertFail() throws DataAccessException {
        authTokenDao.insertAuthToken(authTokenOne);
        authTokenTwo = new AuthToken("asjdflkj23455221", "theMachine");
        assertThrows(DataAccessException.class, () -> authTokenDao.insertAuthToken(authTokenTwo));
    }

    @Test
    public void findPass() throws DataAccessException {
        authTokenDao.insertAuthToken(authTokenOne);
        AuthToken authTokenToCompare = authTokenDao.findAuthToken(authTokenOne.getAuthToken());
        assertEquals(authTokenOne, authTokenToCompare);
    }

    @Test
    public void findFail() throws DataAccessException {
        authTokenDao.insertAuthToken(authTokenTwo);
        AuthToken authTokenToCompare = authTokenDao.findAuthToken(authTokenOne.getAuthToken());
        assertNull(authTokenToCompare);
    }

    @Test
    public void clearTest() throws DataAccessException {
        authTokenDao.insertAuthToken(authTokenOne);
        authTokenDao.insertAuthToken(authTokenTwo);
        authTokenDao.clear();
        AuthToken authTokenToCompare = authTokenDao.findAuthToken(authTokenOne.getAuthToken());
        assertNull(authTokenToCompare);
        authTokenToCompare = authTokenDao.findAuthToken(authTokenTwo.getAuthToken());
        assertNull(authTokenToCompare);
    }
}
