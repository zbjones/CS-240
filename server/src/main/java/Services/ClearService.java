package Services;

import DataAccess.*;
import Results.ClearResult;

/**
 * Accesses the database to delete all data.
 */
public class ClearService {

    private final Database database;
    private final AuthTokenDao authTokenDao;
    private final UserDao userDao;
    private final PersonDao personDao;
    private final EventDao eventDao;

    public ClearService() throws DataAccessException {
        database = new Database();
        authTokenDao = new AuthTokenDao(database.getConnection());
        userDao = new UserDao(database.getConnection());
        personDao = new PersonDao(database.getConnection());
        eventDao = new EventDao(database.getConnection());
    }

    public ClearResult clear() throws DataAccessException {
        try {
            userDao.clear();
            personDao.clear();
            eventDao.clear();
            authTokenDao.clear();
            database.closeConnection(true);
            return new ClearResult("Clear succeeded.", true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            database.closeConnection(false);
            return new ClearResult("Internal Server Error", false);
        }
    }
}
