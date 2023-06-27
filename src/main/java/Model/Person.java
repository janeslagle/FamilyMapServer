package Model;

import java.util.Objects;

/**
 * This class represents any person that we have in the app (the user and any of their relatives from their family map)
 */
public class Person {
    /**
     * Unique identifier for this person
     */
    private String personID;
    /**
     * Username of user to which this person belongs
     */
    private String associatedUsername;
    /**
     * Person’s first name
     */
    private String firstName;
    /**
     * Person's last name
     */
    private String lastName;
    /**
     * Person’s gender
     */
    private String gender;
    /**
     * Person ID of person’s father
     */
    private String fatherID;
    /**
     * Person ID of person's mother
     */
    private String motherID;
    /**
     * Person ID of person's spouse
     */
    private String spouseID;

    /**
     * Create new person within app and get all of their info as well
     * @param personID Unique identifier for this person
     * @param associatedUsername Username of user to which this person belongs
     * @param firstName Person’s first name
     * @param lastName Person's last name
     * @param gender Person's gender
     * @param fatherID Person ID of person's father
     * @param motherID Person ID of person's mother
     * @param spouseID Person ID of person's spouse
     */
    public Person(String personID, String associatedUsername, String firstName, String lastName, String gender,
                String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID= fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    /**
     * Override the equals method to compare two models for equality
     * @param o represents a Person class object so represents a pesron in the app
     * @return boolean for each parameter have in Person class for if the inputted object's parameter and the parameter stored in the class are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person pers = (Person) o;
        return Objects.equals(personID, pers.getPersonID()) && Objects.equals(associatedUsername, pers.associatedUsername)
                && Objects.equals(firstName, pers.firstName) && Objects.equals(lastName, pers.lastName) &&
                Objects.equals(gender, pers.gender) && Objects.equals(fatherID, pers.fatherID) && Objects.equals(motherID, pers.motherID);
    }
}
