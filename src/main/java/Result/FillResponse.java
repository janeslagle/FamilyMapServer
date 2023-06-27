package Result;

/**
 * This is the response to the Fill Request class
 * Response for request to populate DB with data for given username
 */
public class FillResponse {
    /**
     * Message that output for if have error response (if the request was unsuccessful)
     */
    private String message;
    /**
     * bool that tells us if the request was sucessful (so if have success or error response to it)
     */
    private boolean success;

    /**
     * Creates response for the fill request (have same response for successful and failed responses so only need 1 constructor here since both have message and success parameters anyway)
     * @param message Message for if have error response (if the request failed)
     * @param success Tells us if request was successful or not
     */
    public FillResponse(String message, boolean success) {
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
