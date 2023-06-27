package Request;

public class AllPeopleRequest {
    /**
     * Authtoken associated with user want make this request for
     */
    private String authToken;

    /**
     * This creates a request to get all events out
     * @param authToken The authtoken associated with user that want to make this request for
     */
    public AllPeopleRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
