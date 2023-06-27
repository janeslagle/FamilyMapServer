package Service;

import DAO.DataAccessException;
import DAO.Database;
import Result.AllPeopleResponse;
import Request.AllPeopleRequest;
import DAO.*;
import Model.*;

import java.sql.Connection;
import java.util.List;

public class AllPeopleService {
    public AllPeopleResponse AllPeopleService(AllPeopleRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            Connection connection = db.openConnection();

            // Get the username associated with the authtoken that made request with
            // So will need authtoken DAO class obj
            authTokenDAO theTokenDAO = new authTokenDAO(connection);
            Authtoken authTokenGiven = theTokenDAO.getAuthToken(r.getAuthToken());

            // Make sure were given a valid authtoken before move on bc if not given a valid authtoken then no way for us to access the user to get all the people associated with it
            if (authTokenGiven == null) {
                // If it is null, then its not valid and will havea failure response
                db.closeConnection(false);
                AllPeopleResponse result = new AllPeopleResponse("Error: There are no users associated with the given authtoken, you gave an invalid authtoken", false);
                return result;
            }

            // Otherwise, have a valid authtoken so get the user associated with that authtoken
            String theUsername = authTokenGiven.getUsername();

            // Now get ALL of the people associated with that username
            personDAO thePersonDAO = new personDAO(connection);
            List<Person> allPeople = thePersonDAO.getUserPeopleInfo(theUsername);

            // Make sure that the user were given has people assigned to it (otherwise, there are no people to return here)
            if (allPeople == null) {
                // If no people to return then need make a failure object
                db.closeConnection(false);
                AllPeopleResponse result = new AllPeopleResponse("Error: There are no people assigned to the username specified, so there are no people to return here", false);
                return result;
            }

            // Otherwise, can make success reponse oject!
            db.closeConnection(true);
            AllPeopleResponse result = new AllPeopleResponse(allPeople, true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            AllPeopleResponse result = new AllPeopleResponse("Error: Failed to return all people associated with the user" +
                    "specified in the request", false);
            return result;
        }
    }
}
