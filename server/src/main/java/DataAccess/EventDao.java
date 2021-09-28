package DataAccess;

import Models.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Class to manage Event database access.
 * Contains methods to add or remove singular events and methods to retrieve or delete all events attached to a user
 */
public class EventDao {

    private final Connection connection;

    public EventDao(Connection connection)
    {
        this.connection = connection;
    }

    //Clears all events from the database
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Events;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException();
        }
    }

    public void deleteAllEventsAttachedToUser(String username) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        ArrayList<Event> listOfMatchingEvents = new ArrayList<Event>();
        String sql = "DELETE FROM Events WHERE associatedUsername = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException();
        }
    }

    public ArrayList<Event> getAllEventsAttachedToUser(String username) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        ArrayList<Event> listOfMatchingEvents = new ArrayList<>();
        String sql = "SELECT * FROM Events WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                listOfMatchingEvents.add(event);
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
        if (listOfMatchingEvents.isEmpty()) {
            return null;
        }
        else {
            return listOfMatchingEvents;
        }
    }

    public boolean insertEvent(Event event) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insertEvent question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting an Event into the database");
        }
    }

    public Event findEvent(String eventID) throws DataAccessException {
        Event event;
        ResultSet resultSet = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, eventID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                event = new Event(resultSet.getString("EventID"), resultSet.getString("AssociatedUsername"),
                        resultSet.getString("PersonID"), resultSet.getFloat("Latitude"), resultSet.getFloat("Longitude"),
                        resultSet.getString("Country"), resultSet.getString("City"), resultSet.getString("EventType"),
                        resultSet.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
