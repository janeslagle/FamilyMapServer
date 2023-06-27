package Request;

/**
 * This implements the Service AllEvents class
 * This is a request to return ALL events for ALL family members of current user
 * Need authtoken parameter to determine user
 */
public class AllEventsRequest {
    /**
     * Authtoken associated with user want make this request for
     */
    private String authToken;

    /**
     * This creates a request to get all events out
     * @param authToken The authtoken associated with user that want to make this request for
     */
    public AllEventsRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
