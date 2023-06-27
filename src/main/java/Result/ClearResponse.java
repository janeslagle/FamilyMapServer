package Result;

/**
 * This is the response to the request to delete all data from the DB
 */
public class ClearResponse {
    /**
     * Message that output if end up having an error response (if the request was unsuccessful)
     */
    private String message;
    /**
     * bool that tells us if the request was sucessful or not (so if have success or error response to it)
     */
    private boolean success;

    /**
     * Creates response for the clear request (have same response for a successful or failed response since both have only message and success parameters anyway so only need 1 constructor here)
     * @param message Message for if have error response (if the request failed)
     * @param success Tells us if request was successful or not
     */
    public ClearResponse(String message, boolean success) {
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
