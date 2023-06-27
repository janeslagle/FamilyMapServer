package Result;

/**
 * This is the response to making a login request
 */
public class LoginResponse {
    /**
     * The private variables account for both success + error responses
     */

    /**
     * Unique authtoken
     */
    private String authtoken;
    /**
     * Username that is associated with the authtoken
     */
    private String username;
    /**
     * Unique identifier for this person
     */
    private String personID;
    /**
     * message output for if have an error response (for if the register request failed)
     */
    private String message;
    /**
     * bool that tells us if had a successful response or error response to the register request
     */
    private boolean success;

    /**
     * Creates a Response for the login of a user for successful response
     * @param authtoken Unique authtoken
     * @param username Username that is associated with the authtoken
     * @param personID Unique identifier for this person
     * @param success Bool that tells us if had a successful response or error response to the login request
     */
    public LoginResponse(String authtoken, String username, String personID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
    }

    /**
     * Creates a Resposne for the login of a user for failed response
     * @param message Message that output for if have error response (if the request was unsuccessful)
     * @param success Bool that tells us if had a successful response or error resposne to the login request
     */
    public LoginResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getAuthToken() {
        return authtoken;
    }

    public void setAuthToken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
