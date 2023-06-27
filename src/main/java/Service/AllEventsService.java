package Service;

import DAO.DataAccessException;
import DAO.Database;
import DAO.authTokenDAO;
import DAO.eventDAO;
import Model.Authtoken;
import Model.Event;
import Request.AllEventsRequest;
import Result.AllEventsResponse;

import java.sql.Connection;
import java.util.List;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of getting all events info from the DB, to the AllEventsRequest class
 */
public class AllEventsService {
    /**
     * Actually perform the all events request
     * @param r The all events request that want to do
     * @return The result from performing request to get all events
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public AllEventsResponse AllEventsService(AllEventsRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            Connection connection = db.openConnection();

            // Get the username associated with the authtoken that make request with
            // So will need authtoken DAO class obj
            authTokenDAO theTokenDAO = new authTokenDAO(connection);
            Authtoken authTokenGiven = theTokenDAO.getAuthToken(r.getAuthToken());

            // Make sure were given a valid authtoken before move on bc how will find all events for an authtoken that isnt associated with any user
            if (authTokenGiven == null) {
                // If it is null, then its not valid and will have a failure response
                db.closeConnection(false);
                AllEventsResponse result = new AllEventsResponse("Error: There are no users associated with the givenn authtoken, you gave an invalid authtoken", false);
                return result;
            }

            // Get the username that is associated with the given authtoken
            String theUsername = authTokenGiven.getUsername();

            // Now get all events that are associated with that username
            eventDAO theEventDAO = new eventDAO(connection);
            List<Event> allEvents = theEventDAO.findAllUsersEvents(theUsername);

            // Make sure that the username even has any events to find, if there are no events then have a failed response
            if (allEvents == null) {
                db.closeConnection(false);
                AllEventsResponse result = new AllEventsResponse("Error: There were no events assigned to the username specified, so there are no events to return here", false);
                return result;
            }

            // Otherwise, can make success reponse oject!
            db.closeConnection(true);
            AllEventsResponse result = new AllEventsResponse(allEvents, true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            AllEventsResponse result = new AllEventsResponse("Error: Failed to return all events associated with the user" +
                    "specified in the request", false);
            return result;
        }
    }
}
