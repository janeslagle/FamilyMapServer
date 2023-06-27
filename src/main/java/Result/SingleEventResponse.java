package Result;

/**
 * Response to request to return single event obj. with given ID
 * So the response to SingleEvent Request class
 */
public class SingleEventResponse {
    /**
     * Username of user to which this event belongs
     */
    private String associatedUsername;
    /**
     * Unique identitifer for this event
     */
    private String eventID;
    /**
     * ID of person to which this event belongs
     */
    private String personID;
    /**
     * Latitude of event's location
     */
    private float latitude;
    /**
     * Longitude of event's location
     */
    private float longitude;
    /**
     * Country in which event occured
     */
    private String country;
    /**
     * City in which event occured
     */
    private String city;
    /**
     * Type of the event
     */
    private String eventType;
    /**
     * Year in which event occured
     */
    private Integer year;
    /**
     * Message output for if have an error response (for if the register request failed)
     */
    private String message;
    /**
     * Bool that tells you if had a successful response or error response to the request
     */
    private boolean success;

    /**
     * This creates a response to the SingleEvent request when have a successful request
     * @param associatedUsername Username of user to which this event belongs
     * @param eventID Unique identifier for this event
     * @param personID ID of person to which this event belongs
     * @param latitude Latitude of event's location
     * @param longitude Longitude of event's location
     * @param country Country in which event occured
     * @param city City in which event occured
     * @param eventType Type of event
     * @param year Year in which event occured
     * @param success Bool that tells us if had a successful response or error response to the request
     */
    public SingleEventResponse(String associatedUsername, String eventID, String personID,
                               float latitude, float longitude, String country, String city,
                               String eventType, int year, boolean success) {
        this.associatedUsername = associatedUsername;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
        this.success = success;
    }

    /**
     * This creates a resposne to the SingleEvent request when have an error request (when the request fails)
     * @param message Message output for if have an error response (for if the request was sucessful or not)
     * @param success Bool that tells us if had a successful response or error response to the request
     */
    public SingleEventResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
