package Result;

/**
 * Response to the SinglePerson Request class
 * Response for request to populate DB with single person info
 */
public class SinglePersonResponse {
    /**
     * Username of user to which this person belongs
     */
    private String associatedUsername;
    /**
     * Unique identifier for this person
     */
    private String personID;
    /**
     * Person’s first name
     */
    private String firstName;
    /**
     * Person's last name
     */
    private String lastName;
    /**
     * Person’s gender
     */
    private String gender;
    /**
     * Person ID of person’s father
     */
    private String fatherID;
    /**
     * Person ID of person's mother
     */
    private String motherID;
    /**
     * Person ID of person's spouse
     */
    private String spouseID;
    /**
     * Message for whether successful in request or not
     */
    private String message;
    /**
     * Bool that tells us if the request was sucessful (so if have success or error response to it)
     */
    private boolean success;

    /**
     * This creates a response to the SinglePerson Request when have a successful request
     * @param success Bool that tells us if the request was sucessful (so if have success or error response to it)
     * @param associatedUsername Username of user to which this person belongs
     * @param personID Unique identifier for this person
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param gender Person's gender
     * @param fatherID Person ID of person's father
     * @param motherID Person ID of person's mother
     * @param spouseID Person ID of person's spouse
     */
    public SinglePersonResponse(String associatedUsername, String personID,
                            String firstName, String lastName, String gender, String fatherID, String motherID, String spouseID, boolean success) {
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.success = success;
    }

    /**
     * Constructor for when have a failed response with a single person request / service
     * @param message The message that is outputted when have a failed response (gives error message)
     * @param success Bool that says whether have a success or failed response (will be false here since this is the failed response)
     */
    public SinglePersonResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID= fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
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
