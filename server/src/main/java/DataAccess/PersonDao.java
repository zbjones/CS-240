package DataAccess;

import Models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class to manage Person Database Access
 */
public class PersonDao {

    private final Connection connection;

    public PersonDao(Connection connection){
        this.connection = connection;
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Persons;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException();
        }
    }

    public void insertPerson(Person person) throws DataAccessException {
        String sql = "INSERT INTO Persons (personID, associatedUsername, firstName, lastName, "
                + "gender, fatherID, motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting a Person into the database");
        }
    }

    public Person findPerson(String personID) throws DataAccessException {
        Person person;
        ResultSet resultSet = null;
        String sql = "SELECT * FROM Persons WHERE personID = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, personID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                person = new Person(resultSet.getString("personID"), resultSet.getString("associatedUserName"),
                        resultSet.getString("firstName"), resultSet.getString("lastName"),
                        resultSet.getString("gender"), resultSet.getString("fatherID"),
                        resultSet.getString("motherID"), resultSet.getString("spouseID"));
                return person;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while finding a person");
        } finally {
            try {
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public ArrayList<Person> getAllPersonsAttachedToUser(String userName) throws DataAccessException {
        Person Person;
        ResultSet rs = null;
        ArrayList<Person> listOfMatchingPersons = new ArrayList<>();
        String sql = "SELECT * FROM Persons WHERE associatedUserName = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Person = new Person(rs.getString("personID"), rs.getString("associatedUserName"),
                    rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                    rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                listOfMatchingPersons.add(Person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding matching events");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (listOfMatchingPersons.isEmpty()) {
            return null;
        }
        else {
            return listOfMatchingPersons;
        }
    }

    public void deleteAllPersonsAttachedToUser(String userName) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE associatedUserName = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting events attached to a user");
        }
    }
}
