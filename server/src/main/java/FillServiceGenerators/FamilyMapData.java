package FillServiceGenerators;

import Models.Event;
import Models.Person;

import java.util.ArrayList;

/**
 * Plain old Java class to hold the family map data (persons/events) to be inserted into database upon registration/fill api calls
 */
public class FamilyMapData {

    private ArrayList<Person> personArrayList;
    private ArrayList<Event> eventArrayList;

    public FamilyMapData(ArrayList<Person> persons, ArrayList<Event> events) {
        personArrayList = persons;
        eventArrayList = events;
    }

    public ArrayList<Person> getPersonArrayList() {
        return personArrayList;
    }

    public void setPersonArrayList(ArrayList<Person> personArrayList) {
        this.personArrayList = personArrayList;
    }

    public ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public void setEventArrayList(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }
}
