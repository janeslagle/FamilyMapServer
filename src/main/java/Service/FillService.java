package Service;

import DAO.DataAccessException;
import DAO.Database;
import Result.FillResponse;
import Request.FillRequest;
import FakeFamilyTreeData.GenerateFamilyTree;  // Where have method to fill all ancestors for a user!
import DAO.*;
import Model.*;

import java.sql.Connection;
import java.util.UUID;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of filling in all data for user in the DB, to the FillRequest class
 */
public class FillService {
    /**
     * Actually perform the fill request now
     * @param r The fill request itself (has username + generations parameters to use!)
     * @return The result from performing fill request
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public FillResponse FillService(FillRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            Connection connection = db.openConnection();

            // Make userDAO class obj because will need to check if username given in the fill request is a registered user or not
            userDAO theUserDAO = new userDAO(connection);
            User theUser = theUserDAO.getUserInfo(r.getUsername());   // Get the user associated with the inputted username given

            if (theUser == null) {
                // Then means the user has not already been registered with them and need return a fail response bc there is no way can fill the generations for the user when the user doesn't even exist
                db.closeConnection(false);
                FillResponse result = new FillResponse("Error: User has not been registered in the database already so cannot generate ancestors for them", false);
                return result;
            }
            // Generations parameter must be greater than 0, if less than 0 then need return failure object
            if (r.getGenerations() < 0) {
                db.closeConnection(false);
                FillResponse result = new FillResponse("Error: Number of generations was less than 0, it must be a non-negative integer", false);
                return result;
            }

            // Then if get out here, means can actually do what want to do in the fill request!

            // So FIRST delete any existing data in the database already associated with the given username (bc will be replacing that data)
            // This means that we want to clear the people and events associated with that user, so will need methods for that in personDAO and eventDAO
            eventDAO theEventDAO = new eventDAO(connection);
            personDAO thePersonDAO = new personDAO(connection);

            theEventDAO.clearForUser(theUser.getUsername());
            thePersonDAO.clearAllUserPeople(theUser.getUsername());

            GenerateFamilyTree treeData = new GenerateFamilyTree();   // Create GenerateFamilyTree class obj so that can call function to generate all ancestors for the user that need to
            // First need read in the data to make the family tree!
            treeData.ReadInData();

            theUser.setPersonID(UUID.randomUUID().toString());  // Trying to make it so don't have unique personID error when run server by making a new personID for the user working with

            // Then generate all of the ancestors that want to!
            treeData.generateAncestorData(connection, theUser, r.getGenerations());

            // Then generate success object!
            db.closeConnection(true);
            FillResponse result = new FillResponse("Successfully added " + treeData.getNumPeople() + " persons and " + treeData.getNumEvents() + " events to the database.", true);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            FillResponse result = new FillResponse("Error: Failed to populate the servers database with generated data for the specified username.", false);
            return result;
        }
    }
}
