package Model;

import java.util.Objects;

/**
 * This class is used to identify a user as users will have authtokens (IDs identifying their session when they have
 * sucessfully logged in to the app) paired to them. So each user can have multiple tokens associated with them, but
 * they will have one authtoken ID paired to them for each time they are logged in
 */
public class Authtoken {
    /**
     * Unique authtoken, which is a unique string associated with the username for this login session
     */
    private String authToken;
    /**
     * Username that is associated with the authtoken
     */
    private String username;

    /**
     * Creates a new authtoken within the app
     * @param authToken Unique authtoken, associated with the user for this login session
     * @param username Username that is associated with the authtoken
     */
    public Authtoken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Override the equals method to compare two models for equality
     * @param o represents an Authtoken class object so represents an authtoken in the app
     * @return boolean for each parameter have in Authtoken class for if the inputted object's parameter and the parameter stored in the class are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authtoken tok = (Authtoken) o;
        return Objects.equals(authToken, tok.authToken) && Objects.equals(username, tok.username);
    }
}
