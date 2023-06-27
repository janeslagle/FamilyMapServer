package DAO;

import Model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will access the DB to do everything related to people that we will want to be able to do within the app
 * Want be able to create, delete, update and access people in the DB
 */
public class personDAO {
    /**
     * This variable will allow the DAO class to access the database (accesses the DB through this connection)
     */
    private final Connection conn;

    /**
     * This method actually connects the connection variable to the DB
     * @param conn The connection variable to the DB (accesses the DB through this variable)
     */
    public personDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Creates a new person in the DB (same as insert func from EventDAO class)
     * @param person The person that are adding to the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void createNewPerson(Person person) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Person (personID, associatedUsername, firstName, lastName, gender, " +
                "fatherID, motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a person into the database");
        }
    }

    /**
     * Query the DB for all of the people associated with a username (one username can have an entire family map so can have multiple ppl paired with it)
     * Since each person can have multiple people paired with it, want to return a list of Person class objects here
     * @param username The user that want to get all associated people for
     * @return All of the people associated with the inputted username
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public List<Person> getUserPeopleInfo(String username) throws DataAccessException {
        List allPeople = new ArrayList();   // will store all of the events that want to return in this func
        Person person;
        ResultSet rs;
        String sql = "SELECT * FROM Person WHERE AssociatedUsername = ?;";   // find all events associated with 1 user so put its associatedUsername here (events have associatedUsername parameter)
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            // want loop through and get all events
            while (rs.next()) {
                // get each event out of the resultSet
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                // add each event to the list have
                allPeople.add(person);
            }
            // only want return allEVents if actually had stuff inside rs.next so check that actually entered that while loop and added events in
            if (allPeople.size() > 0) {
                return allPeople;
            }
            // if didn't then want return null and not the allEvents list
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an event in the database");
        }
    }

    /**
     * Query DB for the person associated with the given personID, so just searching for 1 person in the DB
     * (same as find function from eventDAO class)
     * @param personID The personID want get person for
     * @return The person associated with that personID in the database if such a person exists, else returns null
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public Person getPersonByID(String personID) throws DataAccessException {
        Person person;
        ResultSet rs;
        String sql = "SELECT * FROM Person WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a person in the database");
        }
    }

    /**
     * Find the person associated with the given fatherID
     * @param fatherID The fatherID that the person will have
     * @return The person associated with that fatherID, the person who has that as their fatherID
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public Person getPersonByFatherID(String fatherID) throws DataAccessException {
        Person person;
        ResultSet rs;
        String sql = "SELECT * FROM Person WHERE fatherID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fatherID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a person in the database");
        }
    }

    /**
     * Find the person associated with the given motherID
     * @param motherID The motherID that the person will have
     * @return The person associated with that motherID, the person who has that as their motherID
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public Person getPersonByMotherID(String motherID) throws DataAccessException {
        Person person;
        ResultSet rs;
        String sql = "SELECT * FROM Person WHERE motherID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, motherID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a person in the database");
        }
    }

    /**
     * Find the person associated with the given spouseID
     * @param spouseID The spouseID that the person will have
     * @return The person associated with that spouseID, the person who has that as their spouseID
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public Person getPersonBySpouseID(String spouseID) throws DataAccessException {
        Person person;
        ResultSet rs;
        String sql = "SELECT * FROM Person WHERE spouseID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spouseID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a person in the database");
        }
    }

    /**
     * Clears the entire Person table, so deletes ALL of the people stored in the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Person";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table");
        }
    }

    /**
     * Might also want to be able to delete all of the people associated with a username (remember each user has multiple people paired with it)
     * @param username The username that want to delete all of the associated people for
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void clearAllUserPeople(String username) throws DataAccessException {
        String sql = "DELETE FROM Person WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the people for the specified user from the Person table");
        }
    }
}
