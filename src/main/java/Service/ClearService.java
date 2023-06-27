package Service;

import DAO.DataAccessException;
import DAO.Database;
import DAO.authTokenDAO;
import DAO.userDAO;
import DAO.eventDAO;
import DAO.personDAO;
import Result.ClearResponse;

import java.sql.Connection;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of clearing all info from the DB, to the ClearRequest class
 */
public class ClearService {
    /**
     * Actually perform the clear request now, don't need an input because just clearing things
     * @return The result from clearing the database like request wants us to do
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public ClearResponse ClearService() throws DataAccessException {
        // Create database class object
        Database db = new Database();

        try {
            // Open DB connection and store it so that can use it to make userDAO class obj
            Connection connection = db.openConnection();

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

            // If cleared them all then should be able to create a success response!
            db.closeConnection(true);
            ClearResponse result = new ClearResponse("Clear succeeded.", true);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            ClearResponse result = new ClearResponse("Error: Failed to clear the tables stored in the database", false);
            return result;
        }
    }
}
