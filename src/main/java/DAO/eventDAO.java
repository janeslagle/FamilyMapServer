package DAO;

import Model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will access the DB to do everything related to events that we want to be able to do in the app
 * With events, will want to insert, query (search over DB for specific or all events for a user) and delete events in the DB
 */
public class eventDAO {
    /**
     * This variable will allow the DAO class to access the database (accesses the DB through this connection)
     */
    private final Connection conn;

    /**
     * This method actually connects the connection variable to the DB
     * @param conn The connection variable to the DB (accesses the DB through this variable)
     */
    public eventDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts an event (from the user's family tree) into the DB
     * @param event The event that are inserting in the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void insert(Event event) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Event (eventID, associatedUsername, personID, latitude, longitude, " +
                "country, city, eventType, year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an event into the database");
        }
    }

    /**
     * Query case 1: search over DB for 1 specific event by searching for the corresponding eventID
     * Will return the event associated with the given eventID from the DB
     * @param eventID The ID associated with the event that want to find in the DB
     * @return The event from the DB associated with the given eventID param if such an event exists in the table, otherwise returns null
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs;
        String sql = "SELECT * FROM Event WHERE eventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("eventID"), rs.getString("associatedUsername"),
                        rs.getString("personID"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                        rs.getString("country"), rs.getString("city"), rs.getString("eventType"),
                        rs.getInt("year"));
                return event;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an event in the database");
        }
    }

    /**
     * Query case 2: search over DB for all events that are associated with a specific user (use their username to do so)
     * Want to be able to grab all events for that user (should be similar to find function with some minor tweaks to account for how it's a list of events returned now)
     * @param username The username for the specific user want to get the events for
     * @return All of the events associated with that given user (return list because a uesr can have multiple events associated with them)
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public List<Event> findAllUsersEvents(String username) throws DataAccessException {
        List allEvents = new ArrayList();   // will store all of the events that want to return in this func
        Event event;
        ResultSet rs;
        String sql = "SELECT * FROM Event WHERE AssociatedUsername = ?;";   // find all events associated with 1 user so put its associatedUsername here (events have associatedUsername parameter)
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            // want loop through and get all events
            while (rs.next()) {
                // get each event out of the resultSet
                event = new Event(rs.getString("eventID"), rs.getString("associatedUsername"),
                        rs.getString("personID"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                        rs.getString("country"), rs.getString("city"), rs.getString("eventType"),
                        rs.getInt("year"));
                // add each event to the list have
                allEvents.add(event);
            }
            // only want return allEVents if actually had stuff inside rs.next so check that actually entered that while loop and added events in
            if (allEvents.size() > 0) {
                return allEvents;
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
     * For finding the birth event for an inserted person (so will query the SQL by personID + eventType)
     * @param personID The person that want to find the birth event for
     * @return The birth event for the person (so will just return 1 event since just finding their birth event only)
     * @throws DataAccessException
     */
    public Event findPersonBirthEvent(String personID) throws DataAccessException {
        Event event;
        ResultSet rs;
        String sql = "SELECT * FROM Event WHERE personID = ? AND eventType = \"birth\";";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("eventID"), rs.getString("associatedUsername"),
                        rs.getString("personID"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                        rs.getString("country"), rs.getString("city"), rs.getString("eventType"),
                        rs.getInt("year"));
                return event;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an event in the database");
        }
    }

    /**
     * For finding the deatg event for an inserted person (so will query the SQL by personID + eventType)
     * @param personID The person that want to find the death event for
     * @return The death event for the person (so will just return 1 event since just finding their death event only)
     * @throws DataAccessException
     */
    public Event findPersonDeathEvent(String personID) throws DataAccessException {
        Event event;
        ResultSet rs;
        String sql = "SELECT * FROM Event WHERE personID = ? AND eventType = \"death\";";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("eventID"), rs.getString("associatedUsername"),
                        rs.getString("personID"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                        rs.getString("country"), rs.getString("city"), rs.getString("eventType"),
                        rs.getInt("year"));
                return event;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an event in the database");
        }
    }

    /**
     * Clears the entire Event table, so deletes ALL of the events stored in the DB for EVERY single user have
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Event";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the event table");
        }
    }

    /**
     * Might not want to delete all events for every single user from the DB, might only want to delete the events
     * associated with one user in the DB
     * @param username The username that want to delete all of the events for in the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void clearForUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Event WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the events for the specified user from the Event table");
        }
    }
}
