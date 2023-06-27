package Service;

import DAO.*;
import Result.LoadResponse;
import Request.LoadRequest;
import Model.*;

import java.sql.Connection;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of loading a new user in the DB, to the LoadRequest class
 */
public class LoadService {
    /**
     * Actually perform the load request now
     * @param r The load request that want to implement
     * @return The result from performing load request
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public LoadResponse LoadService(LoadRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            Connection connection = db.openConnection();

            // Want first clear all data from the DB (just like the clear API so just copy all of that over into here)
            // Create UserDAO, personDAO, eventDAO, authtokenDAO class objects so that can call their clear methods on each of them and clear them all!
            userDAO theUserDAO = new userDAO(connection);
            personDAO thePersonDAO = new personDAO(connection);
            eventDAO theEventDAO = new eventDAO(connection);
            authTokenDAO theAuthtokenDAO = new authTokenDAO(connection);

            // Now call the clear method on each of these to clear their tables!
            theUserDAO.clear();
            thePersonDAO.clear();
            theEventDAO.clear();
            theAuthtokenDAO.clear();

            // Now want to load user, person, event data from request body into the DB
            // From the request body, have lists of users, persons and events want to add SO need to loop through all of those lists and insert them each into DB one by one
            // Init counters for how many users, people and events added in with this request
            int numUsersAdded = 0;
            int numPeopleAdded = 0;
            int numEventsAdded = 0;

            User[] usersWantAdd = r.getUsers();
            Person[] peopleWantAdd = r.getPeople();
            Event[] eventsWantAdd = r.getEvents();

            // First loop through, add all of the users, already have userDAO obj made so use that one
            for (int i = 0; i < usersWantAdd.length; i++) {
                // Insert the user looping through into the database (load it in!)
                theUserDAO.createNewUser(usersWantAdd[i]);

                numUsersAdded++;    // Keep track of number people add
            }
            // Now loop through, add all people into the DB
            for (int i = 0; i < peopleWantAdd.length; i++) {
                thePersonDAO.createNewPerson(peopleWantAdd[i]);
                numPeopleAdded++;
            }
            // Load the events in
            for (int i = 0; i < eventsWantAdd.length; i++) {
                theEventDAO.insert(eventsWantAdd[i]);
                numEventsAdded++;
            }

            // After this, should have a successful response so create the success response!
            db.closeConnection(true);
            LoadResponse result = new LoadResponse("Successfully added " + numUsersAdded + " users, " +
                    numPeopleAdded + " persons, and " + numEventsAdded + " events to the database.", true);
            return result;
        }
        catch (NullPointerException e) {
            db.closeConnection(false);
            LoadResponse result = new LoadResponse("Error: Either one, two or all of the Users, Persons or Events lists inputted in the request body were empty", false);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            LoadResponse result = new LoadResponse("Error: Failed to successfully clear all data from the database and load the user" +
                    "person, and event data from the request body into the database", false);
            return result;
        }
    }
}
