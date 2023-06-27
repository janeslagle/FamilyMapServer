package Request;

/**
 * This implements the Login Service class
 * This is a request to be able to log the user in
 * To be able to log the user in, need their username and password, so will need those 2 parameters for this request
 */
public class LoginRequest {
    /**
     * Unique username for user that is logging in
     */
    private String username;
    /**
     * User’s password for user user that is logging in
     */
    private String password;

    /**
     * This creates a Request to login using the specified username and password
     * @param username Unique username for user
     * @param password User's password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
