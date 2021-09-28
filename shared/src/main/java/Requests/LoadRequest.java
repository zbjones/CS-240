package Requests;

import Models.Event;
import Models.Person;
import Models.User;

/**
 * LoadRequest class is a plain old java class that holds User, Person, and Event arrays to add to the database.
 */
public class LoadRequest {

    private User[] users;
    private Person[] persons;
    private Event[] events;

    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public LoadRequest() {
        users = null;
        persons = null;
        events = null;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
