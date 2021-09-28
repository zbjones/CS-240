package FillServiceGenerators;

import Models.Event;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

/**
 * This class generates events with random location data provided in a json file
 * It is capable of generating events with and without an "eventType" assigned.
 * Publicly called methods return a single Event object
 */
public class LocationGeneration {

    public Event generateEventWithLocation() {
        Event eventToReturn = new Event();
        Random random = new Random();

        try {
            //Get the json location object from the json file
            FileReader fileReader = new FileReader(new File("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\json\\locations.json"));
            JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
            JsonArray locationsArray = (JsonArray) jsonObject.get("data");
            int index = random.nextInt(locationsArray.size());
            JsonObject location = locationsArray.get(index).getAsJsonObject();

            //extract all the contained information in the json object into local variables
            String city = location.get("city").toString();
            city = city.substring(1, city.length() - 1);
            String country = location.get("country").toString();
            country = country.substring(1, country.length() - 1);
            float latitude = location.get("latitude").getAsFloat();
            float longitude = location.get("longitude").getAsFloat();

            //set the event parameters to the contained information
            eventToReturn.setCity(city);
            eventToReturn.setCountry(country);
            eventToReturn.setLatitude(latitude);
            eventToReturn.setLongitude(longitude);
            eventToReturn.setEventType(getEventType());

            return eventToReturn;
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return new Event();
    }

    //Method to generate an event of random type.  Type is selected randomly from a json file.
    public Event generateEventWithTypeAndLocation() {
        Event event = generateEventWithLocation();
        event.setEventType(getEventType());
        return event;
    }

    //Method to randomly select an event type from a json file (for random events)
    private String getEventType() {
        try {
            FileReader fileReader = new FileReader(new File("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\json\\eventtypes.json"));
            return getString(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Helper function to parse the FileReader into Json objects and randomly select a name
    private String getString(FileReader fileReader) {
        Random random = new Random();
        JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
        JsonArray objectArray = (JsonArray) jsonObject.get("data");
        int index = random.nextInt(objectArray.size());
        String object = objectArray.get(index).toString();
        object = object.substring(1, object.length() - 1);

        return object;
    }
}
