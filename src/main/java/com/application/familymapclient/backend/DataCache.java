package com.application.familymapclient.backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import Models.Event;
import Models.Person;


/**
 * Singleton class that stores and sorts Family Map information. Includes sorting and filtering methods.
 */
public class DataCache {

    private static DataCache singletonInstance;
    private final Settings settings = Settings.getSettings();

    private String authToken;

    private Person user;

    private final Set<Person> immediateFamilyMales = new HashSet<>();
    private final Set<Person> immediateFamilyFemales = new HashSet<>();

    private final Set<Person> fatherSideMales = new HashSet<>();
    private final Set<Person> fatherSideFemales = new HashSet<>();
    private final Set<Person> motherSideMales = new HashSet<>();
    private final Set<Person> motherSideFemales = new HashSet<>();

    private final Map<String, Person> personMap = new TreeMap<>();
    private final Map<String, Event> eventMap = new TreeMap<>();
    private final Map<String, Person> filteredPersonMap = new TreeMap<>();
    private final Map<String, Event> filteredEventMap = new TreeMap<>();
    private Map<String, Person> childrenOfEachPerson = new TreeMap<>();

    private final Map<String, String> mapMarkerColors = new TreeMap<>();

    private Person currentPerson;
    private Event currentEvent;

    //Singleton Constructor, we can only have one instance of DataCache
    public static DataCache getDataCache() {
        if (singletonInstance == null) {
            singletonInstance = new DataCache();
        }
        return singletonInstance;
    }

    //Take in Data from Person and Event activities.
    public void importData(ArrayList<Person> persons, ArrayList<Event> events) {
        for (Person person : persons) {
            personMap.put(person.getPersonID(), person);
        }
        for (Event event : events) {
            eventMap.put(event.getEventID(), event);
        }

        for (Person person : personMap.values()) {
            filteredPersonMap.put(person.getPersonID(), person);
        }
        for (Event event : eventMap.values()) {
            filteredEventMap.put(event.getEventID(), event);
        }
    }

    //Sorts the data into chunks for displaying with filters.
    public void structureData(String userPersonID) {
        setUserPerson(userPersonID);
        Person spousePerson = null;
        if (user.getSpouseID() != null) {
            if (personMap.containsKey(user.getSpouseID())) {
                spousePerson = personMap.get(user.getSpouseID());
            }
        }

        //Set user and spouse;
        if (user.getGender().equalsIgnoreCase("m")) {
            immediateFamilyMales.add(user);
            if (spousePerson != null) {
                immediateFamilyFemales.add(spousePerson);
            }
        } else {
            immediateFamilyFemales.add(user);
            if (spousePerson != null) {
                immediateFamilyMales.add(spousePerson);
            }
        }

        sortAncestors();
        setEventColors();
    }

    //Sorts into filters
    private void sortAncestors() {
        sortFathersSideFemales();
        sortMothersSideFemales();
        sortFathersSideMales();
        sortMothersSideMales();
        organizeChildren();
    }

    private void sortMothersSideFemales() {
        recursiveSortFemales(user.getMotherID(), motherSideFemales);
    }

    private void sortMothersSideMales() {
        recursiveSortMales(user.getMotherID(), motherSideMales);
    }

    private void sortFathersSideFemales() {
        recursiveSortFemales(user.getFatherID(), fatherSideFemales);
    }

    private void sortFathersSideMales() {
        recursiveSortMales(user.getFatherID(), fatherSideMales);
    }

    //Recursive function to sort females of one one side
    private void recursiveSortFemales(String personID, Set<Person> setPersons) {
        Person currentPerson = null;

        if (personMap.containsKey(personID)) {
            currentPerson = personMap.get(personID);
        }

        if (currentPerson == null) {
            return;
        }

        if (currentPerson.getGender().equalsIgnoreCase("f")) {
            setPersons.add(currentPerson);
        }

        Person motherPerson = null;

        if (currentPerson.getMotherID() != null) {
            if (personMap.containsKey(currentPerson.getMotherID())) {
                motherPerson = personMap.get(currentPerson.getMotherID());
            }

            if (motherPerson != null) {
                //recursive call
                recursiveSortFemales(motherPerson.getPersonID(), setPersons);
            }
        }

        Person husbandsMother = null;
        Person currentHusband = null;
        if (!currentPerson.equals(personMap.get(user.getMotherID())) &&
                currentPerson.getGender().equalsIgnoreCase("f")) {
            if (personMap.containsKey(currentPerson.getSpouseID())) {
                currentHusband = personMap.get(currentPerson.getSpouseID());
            }
            if (currentHusband.getMotherID() != null) {
                if (personMap.containsKey(currentHusband.getMotherID())) {
                    husbandsMother = personMap.get(currentHusband.getMotherID());
                }

                if (husbandsMother != null) {
                    //recurusive call for spouse
                    recursiveSortFemales(husbandsMother.getPersonID(), setPersons);
                }
            }
        }
    }

    //Recursive function to sort males of one side
    private void recursiveSortMales(String personID, Set<Person> setPersons) {
        Person currentPerson = null;

        if (personMap.containsKey(personID)) {
            currentPerson = personMap.get(personID);
        }

        if (currentPerson == null) {
            return;
        }

        if (currentPerson.getGender().equalsIgnoreCase("m")) {
            setPersons.add(currentPerson);
        }

        Person fatherPerson = null;

        if (currentPerson.getFatherID() != null) {
            if (personMap.containsKey(currentPerson.getFatherID())) {
                fatherPerson = personMap.get(currentPerson.getFatherID());
            }

            if (fatherPerson != null) {
                //recursive call
                recursiveSortMales(fatherPerson.getPersonID(), setPersons);
            }
        }

        Person wifesFather = null;
        Person currentWife = null;
        if (!currentPerson.equals(personMap.get(user.getFatherID())) &&
                currentPerson.getGender().equalsIgnoreCase("m")) {
            if (personMap.containsKey(currentPerson.getSpouseID())) {
                currentWife = personMap.get(currentPerson.getSpouseID());
            }
            if (currentWife.getFatherID() != null) {
                if (personMap.containsKey(currentWife.getFatherID())) {
                    wifesFather = personMap.get(currentWife.getFatherID());
                }
                if (wifesFather != null) {
                    //recursive call for spouse
                    recursiveSortMales(wifesFather.getPersonID(), setPersons);
                }
            }
        }
    }

    //Assigns marker colors (8 total), restarts if # of different events is larger
    private void setEventColors() {
        ArrayList<String> typesOfEvents = new ArrayList<>();

        for (Event event : eventMap.values()) {
            String eventType = event.getEventType();
            if (!typesOfEvents.contains(eventType)) {
                typesOfEvents.add(eventType);
            }
        }

        ArrayList<String> colors = new ArrayList<>();
        colors.add("Red");
        colors.add("Blue");
        colors.add("Green");
        colors.add("Violet");
        colors.add("Cyan");
        colors.add("Orange");
        colors.add("Azure");
        colors.add("Yellow");

        int count = 0;
        for (String type : typesOfEvents) {
            mapMarkerColors.put(type, colors.get(count));
            count++;
            if (count == colors.size()) {
                count = 0;
            }
        }
    }

    //Fetching a Person object by personID, accesses personMap
    public Person getPersonByID(String personID) {
        try {
            return personMap.get(personID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Updates the available filtered persons and events after a settings toggle/filter change
    public void updateFilteredContent() {
        reFilterPersons();
        reFilterEvents();
    }

    private void reFilterPersons() {
        filteredPersonMap.clear();

        if (settings.isMaleEvents()) {
            for (Person male : immediateFamilyMales) {
                filteredPersonMap.put(male.getPersonID(), male);
            }
            if (settings.isFathersSide()) {
                for (Person male : fatherSideMales) {
                    filteredPersonMap.put(male.getPersonID(), male);
                }
            }
            if (settings.isMothersSide()) {
                for (Person male : motherSideMales) {
                    filteredPersonMap.put(male.getPersonID(), male);
                }
            }
        }

        if (settings.isFemaleEvents()) {
            for (Person female : immediateFamilyFemales) {
                filteredPersonMap.put(female.getPersonID(), female);
            }
            if (settings.isFathersSide()) {
                for (Person female : fatherSideFemales) {
                    filteredPersonMap.put(female.getPersonID(), female);
                }
            }
            if (settings.isMothersSide()) {
                for (Person female : motherSideFemales) {
                    filteredPersonMap.put(female.getPersonID(), female);
                }
            }
        }
    }

    //filter events based off of the filtered persons from the previous method (though we do not use filtered person map
    //to actually display info)
    private void reFilterEvents() {
        filteredEventMap.clear();
        Set<String> filteredPersonIDs = filteredPersonMap.keySet();

        for (Event event : eventMap.values()) {
            for (String personID : filteredPersonIDs) {
                if (event.getPersonID().equalsIgnoreCase(personID)) {
                    filteredEventMap.put(event.getEventID(), event);
                    break;
                }
            }
        }
    }

    public Map<String, Person> getRelativesOfPerson(String personID) {
        Map<String, Person> relatedPersons = new TreeMap<>();
        Person person = personMap.get(personID);
        Person mother;
        Person father;
        Person spouse;
        Person child;

        assert person != null;
        if (person.getMotherID() != null) {
            if (personMap.containsKey(person.getMotherID())) {
                mother = personMap.get(person.getMotherID());
                relatedPersons.put("mother", mother);
            }
        }
        if (person.getFatherID() != null) {
            if (personMap.containsKey(person.getFatherID())) {
                father = personMap.get(person.getFatherID());
                relatedPersons.put("father", father);
            }
        }
        if (person.getSpouseID() != null) {
            if (personMap.containsKey(person.getSpouseID())) {
                spouse = personMap.get(person.getSpouseID());
                relatedPersons.put("spouse", spouse);
            }
        }
        if (childrenOfEachPerson.containsKey(person.getPersonID())) {
            child = childrenOfEachPerson.get(person.getPersonID());
            relatedPersons.put("child", child);
        }
        return relatedPersons;
    }

    private void organizeChildren() {
        childrenOfEachPerson.clear();
        for (String personID : personMap.keySet()) {
            Person person = personMap.get(personID);
            if (person.getFatherID() != null) {
                childrenOfEachPerson.put(person.getFatherID(), person);
            }
            if (person.getMotherID() != null) {
                childrenOfEachPerson.put(person.getMotherID(), person);
            }
        }
    }

    //Returns a string defining the relationship between two people. Called by Person Activity
    public String getRelationship(Person currentPerson, Person relativePerson) {
        if (currentPerson.getSpouseID() != null) {
            if (currentPerson.getSpouseID().equals(relativePerson.getPersonID())) {
                return "Spouse";
            }
        }
        if (relativePerson.getMotherID() != null && relativePerson.getFatherID() != null) {
            if (relativePerson.getFatherID().equals(currentPerson.getPersonID()) ||
                    relativePerson.getMotherID().equals(currentPerson.getPersonID())) {
                return "Child";
            }
        }
        if (currentPerson.getFatherID() != null && currentPerson.getMotherID() != null) {
            if (currentPerson.getMotherID().equals(relativePerson.getPersonID())) {
                return "Mother";
            }
            if (currentPerson.getFatherID().equals(relativePerson.getPersonID())) {
                return "Father";
            }
        }
        return "No relation.";
    }

    //Filters and sorts the events for each person
    public List<Event> getFilteredEventsForPerson(String personID) {
        Person person = personMap.get(personID);
        List<Event> personEvents = new ArrayList<>();
        for (Event event : filteredEventMap.values()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                personEvents.add(event);
            }
        }

        //Sort the events by the year
        if (!personEvents.isEmpty()) {
            List<Event> sortedList = new ArrayList<>();
            while (personEvents.size() > 0) {
                Event currEvent = personEvents.get(0);
                int index = 0;
                for (int i = 0; i < personEvents.size(); i++) {
                    if (personEvents.get(i).getYear() < currEvent.getYear()) {
                        currEvent = personEvents.get(i);
                        index = i;
                    }
                }
                sortedList.add(currEvent);
                personEvents.remove(index);
            }
            return sortedList;
        }
        return personEvents;
    }

    //Returns results for the search activity, case insensitive
    public List<Person> getSearchResultsPersons(String query) {
        List<Person> matchingPersons = new ArrayList<>();
        for (Person person : personMap.values()) {
            if (person.getFirstName().toLowerCase().contains(query.toLowerCase())) {
                matchingPersons.add(person);
            } else if (person.getLastName().toLowerCase().contains(query.toLowerCase())) {
                matchingPersons.add(person);
            }
        }
        return matchingPersons;
    }


    //Returns events for the search activity, case insensitive
    public List<Event> getSearchResultsEvents(String query) {
        List<Event> matchingEvents = new ArrayList<>();
        for (Event event : filteredEventMap.values()) {
            if (event.getEventType().toLowerCase().contains(query.toLowerCase())) {
                matchingEvents.add(event);
            } else if (event.getCountry().toLowerCase().contains(query.toLowerCase())) {
                matchingEvents.add(event);
            } else if (event.getCity().toLowerCase().contains(query.toLowerCase())) {
                matchingEvents.add(event);
            } else if (Integer.toString(event.getYear()).contains(query)) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents;
    }

    //Set the person every time the event gets updated
    public void setCurrentEventAndPerson(Event currentEvent) {
        this.currentEvent = currentEvent;
        this.currentPerson = personMap.get(currentEvent.getPersonID());
    }

    public void setUserPerson(String personID) {
        user = personMap.get(personID);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Set<Person> getImmediateFamilyMales() {
        return immediateFamilyMales;
    }

    public Set<Person> getImmediateFamilyFemales() {
        return immediateFamilyFemales;
    }

    public Set<Person> getFatherSideMales() {
        return fatherSideMales;
    }

    public Set<Person> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public Set<Person> getMotherSideMales() {
        return motherSideMales;
    }

    public Set<Person> getMotherSideFemales() {
        return motherSideFemales;
    }

    public String getAuthtoken() {
        return authToken;
    }

    public Map<String, Person> getPersonMap() {
        return personMap;
    }

    public Map<String, Event> getEventMap() {
        return eventMap;
    }

    public Map<String, String> getMapMarkerColors() {
        return mapMarkerColors;
    }

    public Map<String, Person> getFilteredPersonMap() {
        return filteredPersonMap;
    }

    public Map<String, Event> getFilteredEventMap() {
        return filteredEventMap;
    }

    public Person getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(Person currentPerson) {
        this.currentPerson = currentPerson;
    }

    public Map<String, Person> getChildrenOfEachPerson() {
        return childrenOfEachPerson;
    }

    public void setChildrenOfEachPerson(Map<String, Person> childrenOfEachPerson) {
        this.childrenOfEachPerson = childrenOfEachPerson;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }


}
