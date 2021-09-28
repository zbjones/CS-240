package com.application.familymapclient;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.backend.Settings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Models.Event;
import Models.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DataCacheTest {
    Event eventOne;
    Event eventTwo;
    Event eventThree;
    Event eventFour;
    Event eventFive;
    Event eventSix;

    Person personOne;
    Person personTwo;
    Person personThree;
    Person personFour;
    Person personFourSpouse;

    ArrayList<Event> events = new ArrayList<>();
    ArrayList<Person> persons = new ArrayList<>();

    DataCache dataCache = DataCache.getDataCache();


    @Before
    public void setUp() {
        eventOne = new Event("One", "sheila", "NovelPersonID", 80,
                70, "USA", "LA", "event happened", 1999);
        eventTwo = new Event("Two", "username", "PrestonTK", 100,
                50, "Iran", "Tehran", "broke a nuclear centrifuge", 2001);
        eventThree = new Event("Three", "sethie", "Aaaron", 10,
                20, "Georgia", "USSR town", "fought a war", 2005);
        eventFour =  new Event("Four", "sethie", "Carter", 15,
                45, "Sveitsi", "Swiss town", "ate white chocolate", 2008);
        eventFive =  new Event("Five", "sethie", "Olivia", 25,
                45, "Sveitsi", "Swiss town", "ate chocolate", 2006);
        eventSix =  new Event("Six", "sethie", "Carter", 15,
                45, "Sveitsi", "Swiss town", "Birth", 1970);

        personOne = new Person("NovelPersonID", "scottyB", "Scott", "Bruening",
                "M", "TheFather", "TheMother", "TheSpouse");
        personTwo = new Person("PrestonTK", "prestonThePyschMan", "Preston",
                "Keller", "M", "Linda", "Jake", "Amanda");
        personThree = new Person("Aaaron", "aaredd", "Aaron", "Redd",
                "M", "Mother", "father", "Spouse");
        personFour = new Person("Carter", "zbjonesmyman", "Carter",
                "Henderson", "M", "NovelPersonID", "MOTHERID", "Olivia");
        personFourSpouse = new Person("Olivia", "zbjonesmyman",
                "Olivia", "Pluim", "F", "Motherid", "fatherid", "Carter");


        events.add(eventOne);
        events.add(eventTwo);
        events.add(eventThree);
        events.add(eventFour);
        events.add(eventFive);
        events.add(eventSix);

        persons.add(personOne);
        persons.add(personTwo);
        persons.add(personThree);
        persons.add(personFour);
        persons.add(personFourSpouse);

        dataCache.importData(persons, events);
    }

    //Tests importData/getPerson functions
    @Test
    public void importDataTest() {
        Person one = dataCache.getPersonByID("NovelPersonID");
        Assert.assertEquals(personOne, one);

        Person two = dataCache.getPersonByID("PrestonTK");
        Assert.assertEquals(personTwo, two);

        Person three = dataCache.getPersonByID("Aaaron");
        Assert.assertEquals(personThree, three);

        Person four = dataCache.getPersonByID("Carter");
        Assert.assertEquals(personFour, four);

        Event eOne = dataCache.getEventMap().get("One");
        Assert.assertEquals(eventOne, eOne);

        Event eTwo = dataCache.getEventMap().get("Two");
        Assert.assertEquals(eventTwo, eTwo);

        Event eThree = dataCache.getEventMap().get("Three");
        Assert.assertEquals(eventThree, eThree);

        Event eFour = dataCache.getEventMap().get("Four");
        Assert.assertEquals(eventFour, eFour);

        assertNotEquals(personTwo, three);
    }

    @Test
    public void importDataTestFail() {
        Assert.assertNull(dataCache.getPersonByID("Fictitious personID"));
        Assert.assertNull(dataCache.getPersonByID("George H.W. Bush"));

        Assert.assertNull(dataCache.getEventMap().get("Fictitious event"));
        Assert.assertNull(dataCache.getEventMap().get("one"));
    }

    @Test
    public void structureDataTest() {
        dataCache.structureData("Carter");

        Set<Person> spouseSet = dataCache.getImmediateFamilyFemales();
        Person spouse = null;
        for (Person person : spouseSet) {
            spouse = person;
        }
        Assert.assertEquals(personFourSpouse.getLastName(), spouse.getLastName());

        Set<Person> userSet = dataCache.getImmediateFamilyMales();
        Person user = null;
        for (Person person : userSet) {
            user = person;
        }
        Assert.assertEquals(personFour.getMotherID(), user.getMotherID());


        Set<Person> fathersSideMales = dataCache.getFatherSideMales();
        Person father = null;
        for (Person person : fathersSideMales) {
            father = person;
        }
        Assert.assertEquals(personOne.getAssociatedUsername(), father.getAssociatedUsername());

        Map<String, String> eventColors = dataCache.getMapMarkerColors();
        Assert.assertNotNull(eventColors);
        Assert.assertEquals(eventColors.size(), 6);

        for (Map.Entry<String, String> entry : eventColors.entrySet()) {
            Assert.assertNotNull(entry.getValue());
        }
    }

    @Test
    public void structureDataTestFail() {
        Set<Person> userSet = dataCache.getImmediateFamilyMales();
        Person user = null;
        for (Person person : userSet) {
            user = person;
        }
        assertNotEquals(personFourSpouse.getSpouseID(), user.getSpouseID());

        Set<Person> mothersSideFemales = dataCache.getMotherSideFemales();
        Person personToCheck = null;
        for (Person person : mothersSideFemales) {
            personToCheck = person;
        }
        Assert.assertNull(personToCheck);

        Person supposedUser = dataCache.getUser();
        assertNotEquals(personThree.getPersonID(), supposedUser.getPersonID());
    }

    @Test
    public void getRelationshipTest() {
        Assert.assertEquals("Spouse", dataCache.getRelationship(personFour, personFourSpouse));
        Assert.assertEquals("Father", dataCache.getRelationship(personFour, personOne));
        Assert.assertEquals("Child", dataCache.getRelationship(personOne, personFour));
    }

    @Test
    public void getRelationshipFail() {
        assertNotEquals("Spouse", dataCache.getRelationship(personOne, personFour));
        assertNotEquals("Spouse", dataCache.getRelationship(personFourSpouse, personTwo));
        assertNotEquals("Father", dataCache.getRelationship(personOne, personFour));
        assertNotEquals("Mother", dataCache.getRelationship(personOne, personFour));
        assertNotEquals("Father", dataCache.getRelationship(personOne, personFour));
        assertNotEquals("Spouse", dataCache.getRelationship(personTwo, personTwo));
    }

    @Test
    public void filteredEventTest() {
        //Check ordering
        List<Event> carterEvents = dataCache.getFilteredEventsForPerson("Carter");
        Event matchingEvent = carterEvents.get(0);
        Assert.assertEquals(eventSix.getEventType(), matchingEvent.getEventType());
        Assert.assertEquals(eventFour.getEventType(), carterEvents.get(1).getEventType());

        Settings settings = Settings.getSettings();
        settings.setMaleEvents(false);
        dataCache.updateFilteredContent();

        carterEvents = dataCache.getFilteredEventsForPerson("Carter");
        assertTrue("List is empty", carterEvents.isEmpty());

        settings = Settings.getSettings();
        settings.setMaleEvents(true);
        settings.setFemaleEvents(false);
        dataCache.updateFilteredContent();

        assertTrue(dataCache.getFilteredEventsForPerson("Olivia").isEmpty());

        settings.setFemaleEvents(true);
        dataCache.updateFilteredContent();
    }

    @Test
    public void filteredEventTestFail() {
        List<Event> carterEvents = dataCache.getFilteredEventsForPerson("Carter");
        Event matchingEvent = carterEvents.get(0);
        assertNotEquals(eventFour.getEventType(), matchingEvent.getEventType());
        assertNotEquals(eventFive.getEventType(), carterEvents.get(1).getEventType());

        carterEvents = dataCache.getFilteredEventsForPerson("Carter");
        matchingEvent = carterEvents.get(0);
        assertNotEquals(eventThree.getCity(), matchingEvent.getCity());

        Settings settings = Settings.getSettings();
        settings.setMaleEvents(false);
        dataCache.updateFilteredContent();

        carterEvents = dataCache.getFilteredEventsForPerson("Carter");
        List<Event> novelEvents = dataCache.getFilteredEventsForPerson("NovelPersonID");
        List<Event> threeEvents = dataCache.getFilteredEventsForPerson("Aaaron");
        List<Event> twoEvents = dataCache.getFilteredEventsForPerson("PrestonTK");
        assertTrue(carterEvents.isEmpty());
        assertTrue(novelEvents.isEmpty());
        assertTrue(threeEvents.isEmpty());
        assertTrue(twoEvents.isEmpty());

        List<Event> femaleEvents = dataCache.getFilteredEventsForPerson("Olivia");
        Assert.assertFalse(femaleEvents.isEmpty());
        assertNotEquals(femaleEvents.get(0).getEventID(), eventFour.getEventID());
    }

    @Test
    public void searchPeopleTest() {
        List<Person> searchResults = dataCache.getSearchResultsPersons("Cart");
        assertEquals("Carter", searchResults.get(0).getPersonID());
        assertEquals(1, searchResults.size());

        searchResults = dataCache.getSearchResultsPersons("Preston");
        assertEquals("PrestonTK", searchResults.get(0).getPersonID());
        assertEquals(1, searchResults.size());

        searchResults = dataCache.getSearchResultsPersons("Oli");
        assertEquals("Olivia", searchResults.get(0).getPersonID());
        assertEquals(1, searchResults.size());
    }

    @Test
    public void searchPeopleTestFail() {
        List<Person> searchResults = dataCache.getSearchResultsPersons("Carterr");
        assertTrue(searchResults.isEmpty());

        searchResults = dataCache.getSearchResultsPersons("Alisha");
        assertTrue(searchResults.isEmpty());

        searchResults = dataCache.getSearchResultsPersons(".");
        assertTrue(searchResults.isEmpty());
    }

    @Test
    public void searchEventTest() {
        List<Event> eventResults = dataCache.getSearchResultsEvents("sveit");
        assertEquals(3, eventResults.size());

        eventResults = dataCache.getSearchResultsEvents("town");
        assertEquals(4, eventResults.size());

        eventResults = dataCache.getSearchResultsEvents("chocolat");
        assertEquals(2, eventResults.size());

        eventResults = dataCache.getSearchResultsEvents("200");
        assertEquals(4, eventResults.size());
    }

    @Test
    public void searchEventTestFail() {
        List<Event> eventResults = dataCache.getSearchResultsEvents("swisss");
        assertTrue(eventResults.isEmpty());

        eventResults = dataCache.getSearchResultsEvents("2000");
        assertTrue(eventResults.isEmpty());

        eventResults = dataCache.getSearchResultsEvents("Carter");
        assertTrue(eventResults.isEmpty());

        eventResults = dataCache.getSearchResultsEvents("200.8");
        assertTrue(eventResults.isEmpty());
    }
}