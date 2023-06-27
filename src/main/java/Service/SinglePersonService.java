package Service;

import DAO.DataAccessException;
import DAO.Database;
import Result.SinglePersonResponse;
import Request.SinglePersonRequest;
import DAO.*;
import Model.*;

import java.sql.Connection;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of getting single person info from DB, to the SinglePersonRequest class
 */
public class SinglePersonService {
    /**
     * Actually perform the single person request now
     *
     * @param r The single person request that want to do
     * @return The result from performing single person request
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public SinglePersonResponse SinglePersonService(SinglePersonRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            Connection connection = db.openConnection();

            // Get the username associated with the authtoken that make request with
            // So will need authtoken DAO class obj
            authTokenDAO theTokenDAO = new authTokenDAO(connection);
            Authtoken authTokenGiven = theTokenDAO.getAuthToken(r.getAuthToken());

            // Make sure were given a valid authtoken before move on
            if (authTokenGiven == null) {
                // If it is null, then its not valid and will havea failure response
                db.closeConnection(false);
                SinglePersonResponse result = new SinglePersonResponse("Error: There is no user associated with the given authtoken, you gave an invalid authtoken", false);
                return result;
            }

            String theUsername = authTokenGiven.getUsername();

            // Now get the person associated with that username and given personID
            personDAO thePersonDAO = new personDAO(connection);
            Person thePerson = thePersonDAO.getPersonByID(r.getPersonID());

            // Make sure the person / personID is valid, if not then need create a failure response
            if (thePerson == null) {
                // If the person is null it means that were given an invalid personID
                db.closeConnection(false);
                SinglePersonResponse result = new SinglePersonResponse("Error: There is no person associated with the given personID, you gan an invalid personID", false);
                return result;
            }

            // Make sure the username for the person just found matches the username associated with the auth token inputted
            if (!thePerson.getAssociatedUsername().equals(theUsername)) {
                db.closeConnection(false);
                SinglePersonResponse result = new SinglePersonResponse("Error: The username of the person associated with the inputted personID doesn't match the username associated with the given authtoken, invalid request", false);
                return result;
            }

            // Otherwise, if make it here then can make success response object!
            db.closeConnection(true);
            SinglePersonResponse result = new SinglePersonResponse(thePerson.getAssociatedUsername(), thePerson.getPersonID(),
                    thePerson.getFirstName(), thePerson.getLastName(), thePerson.getGender(),
                    thePerson.getFatherID(), thePerson.getMotherID(), thePerson.getSpouseID(), true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            SinglePersonResponse result = new SinglePersonResponse("Error: Failed to return the single person object that was " +
                    "specified in the request", false);
            return result;
        }
    }
}
