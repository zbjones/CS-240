package Models;


import java.util.UUID;

/**
 * Provided class.
 * Plain old Java class modeling an Event database object.
 * The empty constructor will initialize the eventID parameter using UUID (ensures we get a unique EventID so collisions in the database
 * don't happen.)
 */
public class Event {

    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    public Event(String eventID, String username, String personID, float latitude, float longitude,
                 String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = username;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public Event() {
        eventID = UUID.randomUUID().toString();
    }

    /**
     * Get the Event ID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Set the Event ID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Get the user's name
     */
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    /**
     * Set the user's name
     */
    public void setUsername(String username) {
        this.associatedUsername = username;
    }

    /**
     * Get the Person's ID
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Set the Person's ID
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Get the Latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Set the Latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the Longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Set the Longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the Country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the City
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get Event Type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Set the Event Type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Get the Year
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the Year
     */
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Event) {
            Event oEvent = (Event) o;
            return oEvent.getEventID().equals(getEventID()) &&
                    oEvent.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oEvent.getPersonID().equals(getPersonID()) &&
                    oEvent.getLatitude() == (getLatitude()) &&
                    oEvent.getLongitude() == (getLongitude()) &&
                    oEvent.getCountry().equals(getCountry()) &&
                    oEvent.getCity().equals(getCity()) &&
                    oEvent.getEventType().equals(getEventType()) &&
                    oEvent.getYear() == (getYear());
        } else {
            return false;
        }
    }
}
