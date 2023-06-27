package Result;

/**
 * This is the response to the Load Request class
 * Response for request to load user, person, event data into DB
 */
public class LoadResponse {
    /**
     * Message that output for if have error response (if the request was unsuccessful)
     */
    private String message;
    /**
     * bool that tells us if the request was sucessful (so if have success or error response to it)
     */
    private boolean success;

    /**
     * Creates response for the load request (same successful and failed response since both have message and success as only parameters anyway so only need 1 constructor here)
     * @param message Message for if have error response (if the request failed)
     * @param success Tells us if request was successful or not
     */
    public LoadResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
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
