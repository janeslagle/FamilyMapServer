package Request;

/**
 * End up calling this function when make the fill request / implement the fill service
 * Don't actually have a request body, need to get the request from the urlpath inputted by the user, so this is different than the other requests
 */
public class FillRequest {
    /**
     * The username that want to make the fill request for
     */
    private String username;
    /**
     * The number of generations that want to "fill" for the username specified in the request
     */
    private int generations;

    /**
     * Request for doing a fill implementation of the fill request service thing
     * @param username The username that want to generate / fill the ancestors for
     * @param generations The number of generations that want to "fill" for the specified username
     */
    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }
}
