package Result;

/**
 * This is the response to making a register request
 * Have 2 possible responses: either new user account was successfully registered or new user account failed to be created
 */
public class RegisterResponse {
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
     * Creates a Response for the registering of a new user when the request is successful (this covers the success response body)
     * @param authtoken Unique authtoken
     * @param username Username that is associated with the authtoken
     * @param personID Unique identifier for this person
     * @param success Bool that tells us if had a successful response or error response to the register request
     */
    public RegisterResponse(String authtoken, String username, String personID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
    }

    /**
     * Creates a Response for register request when the request fails (when get an error response)
     * @param message Message output for if have an error response (if register request failed)
     * @param success Bool that tells us if had successful response or error response to the register request
     */
    public RegisterResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
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
