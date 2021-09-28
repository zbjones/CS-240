package FillServiceGenerators;

import Models.Event;
import Models.Person;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class handles generation of all Events.
 * The class holds a username object for the username to which the event should be associated.
 * ALl of the created events are contained within an ArrayList<Event> and should be accessed and returned by the Generator method.
 * Contains an instance of the LocationGeneration class to help create "stock", random objects
 */
public class EventGenerator {

    private final LocationGeneration locationGeneration = new LocationGeneration();
    private final Random random = new Random();
    private static String username;
    private ArrayList<Event> eventArrayList;

    public EventGenerator(String username) {
        EventGenerator.username = username;
        eventArrayList = new ArrayList<Event>();
    }

    //Create a birth event for any given person and the current year.
    public void createBirthEvent(Person person, int currentYear) {
        Event birthEvent = locationGeneration.generateEventWithLocation();
        int birthYear = currentYear - random.nextInt(10);

        birthEvent.setEventType("Birth");
        birthEvent.setUsername(username);
        birthEvent.setPersonID(person.getPersonID());
        birthEvent.setYear(birthYear);

        eventArrayList.add(birthEvent);
    }

    //create a death event for any given person and the current year.
    public void createDeathEvent(Person person, int currentYear) {
        Event deathEvent = locationGeneration.generateEventWithLocation();
        int minimumLifespan = 25;

        int deathYear = currentYear + minimumLifespan + random.nextInt(50);

        //People cannot die in the future
        if (deathYear > 2020) {
            deathYear = 2020;
        }

        deathEvent.setUsername(username);
        deathEvent.setPersonID(person.getPersonID());
        deathEvent.setEventType("Death");
        deathEvent.setYear(deathYear);

        eventArrayList.add(deathEvent);
    }

    //Given two people and any given year, create a marriage event.
    public void marriageEvent(Person man, Person woman, int currentYear) {
        int realisticMarriageAge = 15;
        int marriageYear = currentYear + realisticMarriageAge + random.nextInt(15);

        Event marriageEventMan = locationGeneration.generateEventWithLocation();

        //Add local parameters for the man's wedding event
        marriageEventMan.setUsername(username);
        marriageEventMan.setEventType("Marriage");
        marriageEventMan.setPersonID(man.getPersonID());
        marriageEventMan.setYear(marriageYear);

        //Create a new wedding event for the woman, replace the personID parameter with the woman's personID
        Event marriageEventWoman = new Event();
        marriageEventWoman.setPersonID(woman.getPersonID());
        marriageEventWoman.setEventType("Marriage");
        marriageEventWoman.setUsername(username);
        marriageEventWoman.setYear(marriageYear);
        marriageEventWoman.setPersonID(woman.getPersonID());
        marriageEventWoman.setCity(marriageEventMan.getCity());
        marriageEventWoman.setCountry(marriageEventMan.getCountry());
        marriageEventWoman.setLatitude(marriageEventMan.getLatitude());
        marriageEventWoman.setLongitude(marriageEventMan.getLongitude());

        eventArrayList.add(marriageEventMan);
        eventArrayList.add(marriageEventWoman);
    }

    //Creates a random event for any given person.
    public void randomEvent(Person person, int currentYear) {
        int eventYear = currentYear + 5 + random.nextInt(19);

        Event randomEvent = locationGeneration.generateEventWithTypeAndLocation();
        randomEvent.setUsername(username);
        randomEvent.setPersonID(person.getPersonID());
        randomEvent.setYear(eventYear);
    }

    public ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public void setEventArrayList(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }
}
