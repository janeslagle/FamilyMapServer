package Request;

/**
 * This implements the Service SingleEvent class
 * This is a request to return SINGLE event obj with specified eventID
 * Need authtoken to determine current user
 * So need those 2 parameters to make a request, the eventID and the authtoken
 */
public class SingleEventRequest {
    /**
     * The eventID associated with the event want to return
     */
    private String eventID;
    /**
     * The authtoken associated with the user want see that the eventID corresponds with
     */
    private String authToken;

    /**
     * This creates a request for a new single event to be found
     * @param eventID the ID associated with the event that want to find
     * @param authToken the authtoken associated with the user who has this eventID paired with them
     */
    public SingleEventRequest(String eventID, String authToken) {
        this.eventID = eventID;
        this.authToken = authToken;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
