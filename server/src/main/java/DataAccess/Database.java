package DataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Provided class to manage database operations.
 * Contains a static connection object.
 */
public class Database {
    private static Connection connection;

    //Whenever we want to make a change to our database we will have to open a connection and use
    //Statements created by that connection to initiate transactions
    public Connection openConnection() throws DataAccessException {
        try {
            //The Structure for this Connection is driver:language:path
            //The path assumes you start in the root of your project unless given a non-relative path
            final String CONNECTION_URL = "jdbc:sqlite:familymap.sqlite";

            // Open a database connection to the file given in the path
            connection = DriverManager.getConnection(CONNECTION_URL);

            // Start a transaction
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to open connection to database");
        }
        return connection;
    }

    public Connection getConnection() throws DataAccessException {
        if(connection == null) {
            return openConnection();
        } else {
            return connection;
        }
    }

    //When we are done manipulating the database it is important to close the connection. This will
    //End the transaction and allow us to either commit our changes to the database or rollback any
    //changes that were made before we encountered a potential error.

    //IMPORTANT: IF YOU FAIL TO CLOSE A CONNECTION AND TRY TO REOPEN THE DATABASE THIS WILL CAUSE THE
    //DATABASE TO LOCK. YOUR CODE MUST ALWAYS INCLUDE A CLOSURE OF THE DATABASE NO MATTER WHAT ERRORS
    //OR PROBLEMS YOU ENCOUNTER
    public void closeConnection(boolean commit) throws DataAccessException {
        try {
            if (connection != null) {
                if (commit) {
                    //This will commit the changes to the database
                    connection.commit();
                } else {
                    //If we findEvent out something went wrong, pass a false into closeConnection and this
                    //will rollback any changes we made during this connection
                    connection.rollback();
                }
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to close database connection");
        }
    }

    //This method clears the tables of the the database by dropping all of them if they exist.
    public void clearTables() throws DataAccessException {
        try (Statement stmt = connection.createStatement()){
            String sql = "DROP TABLE IF EXISTS Events; " +
                    "DROP TABLE IF EXISTS Users; " +
                    "DROP TABLE IF EXISTS AuthToken; " +
                    "DROP TABLE IF EXISTS Persons;";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing tables.");
        }
    }

    //This function creates the necessary tables for the FamilyMapServer database
    public void createTables() throws DataAccessException {
        try(Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Users (" +
                    "username varchar(255) not null primary key unique, " +
                    "password varchar(255) not null, " +
                    "email varchar(255) not null, " +
                    "firstName varchar(255) not null, " +
                    "lastName varchar(255) not null, " +
                    "gender varchar(2) not null, " +
                    "personID varchar(255) not null unique, " +
                    "foreign key(personID) references Persons(personID) );" +

                    "CREATE TABLE IF NOT EXISTS Events (" +
                    "eventId varchar(255) not null primary key unique, " +
                    "associatedUsername varchar(255) not null, " +
                    "personID varchar(255) not null, " +
                    "latitude real not null, " +
                    "longitude real not null, " +
                    "country varchar(255) not null, " +
                    "city varchar(255) not null, " +
                    "eventType varchar(255) not null, " +
                    "year int not null, " +
                    "foreign key(associatedUsername) references Users(username), " +
                    "foreign key(personID) references Persons(personID) );" +

                    "CREATE TABLE IF NOT EXISTS AuthToken (" +
                    "token varchar(255) not null primary key unique, " +
                    "username varchar(255) not null, " +
                    "foreign key(username) references Users(username) );" +

                    "CREATE TABLE IF NOT EXISTS Persons (" +
                    "personID varchar(255) not null primary key unique, " +
                    "associatedUsername varchar(255) not null, " +
                    "firstName varchar(255) not null, " +
                    "lastName varchar(255) not null, " +
                    "gender varchar(2) not null, " +
                    "fatherID varchar(255), " +
                    "motherID varchar(255), " +
                    "spouseID varchar(255), " +
                    "foreign key(associatedUsername) references Users(username), " +
                    "foreign key(fatherID) references Persons(personID), " +
                    "foreign key(motherID) references Persons(personID), " +
                    "foreign key(spouseID) references Persons(personID) );";

            stmt.executeUpdate(sql);

        } catch (SQLException exception) {
            throw new DataAccessException("SQL Error encountered while creating new tables.");
        }
    }
}

