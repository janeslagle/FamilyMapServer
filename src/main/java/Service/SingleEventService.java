package Service;

import DAO.DataAccessException;
import DAO.Database;
import DAO.authTokenDAO;
import DAO.eventDAO;
import Model.Authtoken;
import Model.Event;
import Request.SingleEventRequest;
import Result.SingleEventResponse;

import java.sql.Connection;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of getting single event info from DB, to the SingleEventRequest class
 */
public class SingleEventService {
    /**
     * Actually perform the single event request now
     * @param r The single event request want to perform
     * @return The result from performing single event request
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public SingleEventResponse SingleEventService(SingleEventRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            Connection connection = db.openConnection();

            // Get the username associated with the authtoken that make request with
            // So will need authtoken DAO class obj
            authTokenDAO theTokenDAO = new authTokenDAO(connection);
            Authtoken authTokenGiven = theTokenDAO.getAuthToken(r.getAuthToken());

            // Make sure were given a valid authtoken before move on, otherwise cant get the eventID bc cant access the person
            // Case where given a wrong authtoken
            if (authTokenGiven == null) {
                // If it is null, then its not valid and will havea failure response
                db.closeConnection(false);
                SingleEventResponse result = new SingleEventResponse("Error: There is no user associated with the given authtoken, you gave an invalid authtoken", false);
                return result;
            }

            // Now get the username for the user associated with that authtoken
            String theUsername = authTokenGiven.getUsername();

            // Now get the event associated with that username
            eventDAO theEventDAO = new eventDAO(connection);
            Event theEvent = theEventDAO.find(r.getEventID());

            // Make sure event is valid, if not then need create a failure response bc there is no event to return!
            // Case where given a wrong eventID
            if (theEvent == null) {
                db.closeConnection(false);
                SingleEventResponse result = new SingleEventResponse("Error: There is no event associated with the given eventID, you gave an invalid eventID", false);
                return result;
            }

            // Make sure the username for the person just found matches the username associated with the auth token inputted
            if (!theEvent.getAssociatedUsername().equals(theUsername)) {
                db.closeConnection(false);
                SingleEventResponse result = new SingleEventResponse("Error: The username of the event doesn't match the username of the given authtoken, invalid request", false);
                return result;
            }

            // Otherwise, can make success reponse oject!
            db.closeConnection(true);
            SingleEventResponse result = new SingleEventResponse(theEvent.getAssociatedUsername(), theEvent.getEventID(),
                    theEvent.getPersonID(), theEvent.getLatitude(), theEvent.getLongitude(),
                    theEvent.getCountry(), theEvent.getCity(), theEvent.getEventType(), theEvent.getYear(), true);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            SingleEventResponse result = new SingleEventResponse("Error: Failed to return the single event object that was " +
                    "specified in the request", false);
            return result;
        }
    }
}