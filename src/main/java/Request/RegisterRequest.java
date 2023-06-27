package Request;

/**
 * This class implements the Service Register class
 * Follows structure of JSON in Java given in Web API section of lab spec
 * This is a request to be able to register/ create a new user in the DB
 */
public class RegisterRequest {
    /**
     * Unique username for user that are trying to create
     */
    private String username;
    /**
     * User’s password for user that are trying to create
     */
    private String password;
    /**
     * User’s email address for user that are trying to create
     */
    private String email;
    /**
     * User’s first name for new user that are creating
     */
    private String firstName;
    /**
     * User's last name for new user that are creating
     */
    private String lastName;
    /**
     * User's gender
     */
    private String gender;

    /** This creates a Request to register/create a new user in the DB
     * @param username Unique username for new user
     * @param password New user's password
     * @param email New user's email address
     * @param firstName New user's first name
     * @param lastName New user's last name
     * @param gender New user's gender
     */
    public RegisterRequest(String username, String password, String email, String firstName, String lastName,
                String gender) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
