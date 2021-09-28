package FillServiceGenerators;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

/**
 * This class handles name generation and selects randomly from three provided JSON documents to select male, female, and last names.
 */
public class NameGenerator {

    public String getMaleName() {
        try {
            FileReader fileReader = new FileReader(new File("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\json\\mnames.json"));
            return getString(fileReader);
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String getFemaleName() {
        try {
            FileReader fileReader = new FileReader(new File("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\json\\fnames.json"));
            return getString(fileReader);
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String getLastName() {
        try {
            FileReader fileReader = new FileReader(new File("C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\json\\snames.json"));
            return getString(fileReader);
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    //Helper function to parse the FileReader into Json objects and randomly select a name
    private String getString(FileReader fileReader) {
        Random random = new Random();
        JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
        JsonArray nameArray = (JsonArray) jsonObject.get("data");
        int index = random.nextInt(nameArray.size());
        String name = nameArray.get(index).toString();
        name = name.substring(1, name.length() - 1);
        return name;
    }
}
