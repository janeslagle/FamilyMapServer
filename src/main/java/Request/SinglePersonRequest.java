package Request;

/**
 * Implements Service SinglePerson Class
 * This is a request to be able to return SINGLE person obj with specified ID (if person assoc. with current user)
 * The current user is determined by the provided authtoken so need the personID, authtoken parameters to
 */
public class SinglePersonRequest {
    /**
     * The personID for the user want to return single person obj for
     */
    private String personID;
    /**
     * The authtoken associated with the user want to work with
     */
    private String authToken;

    /**
     * This creates a request to get the single person obj associated with the authtoken user
     * @param personID The personID for the user want to return single person obj for
     * @param authToken The authtoken associated with the user want to work with
     */
    public SinglePersonRequest(String personID, String authToken) {
        this.personID = personID;
        this.authToken = authToken;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
