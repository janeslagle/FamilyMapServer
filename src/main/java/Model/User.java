package Model;

import java.util.Objects;

/**
 * Every person using the app is auser identified by their unique username login
 * Every user is attached to a person, username + password (an account for the app)
 */
public class User {
    /**
     * Unique username for user
     */
    private String username;
    /**
     * User’s password
     */
    private String password;
    /**
     * User’s email address
     */
    private String email;
    /**
     * User’s first name
     */
    private String firstName;
    /**
     * User's last name
     */
    private String lastName;
    /**
     * User's gender
     */
    private String gender;
    /**
     * Unique Person ID assigned to this user’s generated Person
     */
    private String personID;

    /**
     * Create a new user account within in the app
     * @param username Unique username for user
     * @param password User's password
     * @param email User's email address
     * @param firstName User's first name
     * @param lastName User's last name
     * @param gender User's gender
     * @param personID Unique ID assigned to this user's genderated Person
     */
    public User(String username, String password, String email, String firstName, String lastName,
                 String gender, String personID) {
       this.username = username;
       this.password = password;
       this.email = email;
       this.firstName = firstName;
       this.lastName = lastName;
       this.gender = gender;
       this.personID = personID;
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

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Override the equals method to compare two models for equality
     * @param o represents a User class object so represents a user in the app
     * @return boolean for each parameter have in User class for if the inputted object's parameter and the parameter stored in the class are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(gender, user.gender) && Objects.equals(personID, user.personID);
    }
}
