package FillServiceGenerators;


import Models.Event;
import Models.Person;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generator class contains an instance of EventGenerator and NameGenerator.
 *
 */
public class Generator {

    private final EventGenerator eventGenerator;
    private ArrayList<Event> eventArrayList;
    private final ArrayList<Person> personArrayList;
    private final NameGenerator nameGenerator;
    String username;
    private final Random random;
    int numGenerations;
    Person user;

    public Generator(Person person, int numGenerations) {
        user = person;
        username = person.getAssociatedUsername();
        eventGenerator = new EventGenerator(username);
        nameGenerator = new NameGenerator();
        random = new Random();
        this.numGenerations = numGenerations;
        personArrayList = new ArrayList<>();
        eventArrayList = new ArrayList<>();
    }

    //Wrapper for recursion generation calls
    public FamilyMapData generateFamilyMap() {
        createFamilyTree(user, numGenerations);
        eventArrayList = eventGenerator.getEventArrayList();

        return new FamilyMapData(personArrayList, eventArrayList);
    }

    //Parent method that calls the recursive function createMotherAndFather();
    private void createFamilyTree(Person personUser, int numGenerations) {
        //Add the root person and a corresponding birth event
        personArrayList.add(personUser);
        int currentYear = 2021;
        eventGenerator.createBirthEvent(personUser, currentYear);

        //Call the recursive function to create mothers and fathers.
        createMotherAndFather(personUser, numGenerations, currentYear);
    }

    //Recursive function to repeatedly create mothers and fathers for each person, number of generations left
    // and the current year is required
    private void createMotherAndFather(Person currentPerson, int numGenerations, int currentYear) {
        int averageParenthoodAge = 40;
        currentYear = currentYear - averageParenthoodAge + random.nextInt(5);

        //Create mother and father objects and give them random names
        Person father = new Person();
        Person mother = new Person();
        String fatherName = nameGenerator.getMaleName();
        String motherName = nameGenerator.getFemaleName();
        String surname = nameGenerator.getLastName();
        String fatherID = makePersonID(fatherName, surname);
        String motherID = makePersonID(motherName, surname);

        //Set the father mother of the current person for whom we are creating parents
        currentPerson.setFatherID(fatherID);
        currentPerson.setMotherID(motherID);

        //Set all parameters for the father
        father.setFirstName(fatherName);
        father.setLastName(surname);
        father.setPersonID(fatherID);
        father.setSpouseID(motherID);
        father.setAssociatedUsername(username);
        father.setGender("m");

        //Set all parameters for the mother
        mother.setFirstName(motherName);
        mother.setLastName(surname);
        mother.setPersonID(motherID);
        mother.setSpouseID(fatherID);
        mother.setAssociatedUsername(username);
        mother.setGender("f");

        //Add the mother and father to the ArrayList of persons
        personArrayList.add(mother);
        personArrayList.add(father);

        //Create life events for the mother and father
        addLifeEvents(mother, father, currentYear);

        //Decrement the number of generations before the recursive call
        --numGenerations;

        if (numGenerations != 0) {
            createMotherAndFather(father, numGenerations, currentYear);
            createMotherAndFather(mother, numGenerations, currentYear);
        }
    }

    //Create some life events for each person
    private void addLifeEvents(Person mother, Person father, int currentYear) {
        //Create the necessary birth, marriage, and death events
        eventGenerator.createBirthEvent(mother, currentYear);
        eventGenerator.createBirthEvent(father, currentYear);
        eventGenerator.marriageEvent(father, mother, currentYear);
        eventGenerator.createDeathEvent(father, currentYear);
        eventGenerator.createDeathEvent(mother, currentYear);
    }

    //Create a unique personID from the person's first and last name and a random two digit number
    private String makePersonID(String first, String surname) {
        return first + "_" + surname + random.nextInt(100);
    }
}
