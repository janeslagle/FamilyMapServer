package Service;

import DAO.DataAccessException;
import DAO.Database;
import Request.RegisterRequest;
import Result.RegisterResponse;
import DAO.*;
import Model.*;
import FakeFamilyTreeData.*;

import java.sql.Connection;
import java.util.UUID;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of registering a new user in the DB, to the RegisterRequest class
 */
public class RegisterService {
    /**
     * Actually performs the register request now
     * @param r The request to register a new user
     * @return The result from performing new register of user (will return an authtoken for the user created)
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public RegisterResponse RegisterService(RegisterRequest r) throws DataAccessException {
        // Follow the service class example were given in implementation tips video

        // Create database class object
        Database db = new Database();

        try {
            // Open DB connection and store it so that can use it to make userDAO class obj
            Connection connection = db.openConnection();

            // Create random, unique UUID class string to use for personID below
            UUID uuid = UUID.randomUUID();
            String wantedPersonID = uuid.toString();  // This actually creates the random, unique string want use for the personID

            // Create userDAO class obj so that can make user, etc. (want register a user here)
            userDAO theUserDAO = new userDAO(connection);

            // Create authTokebDAO class obj (will need return an authtoken so will need this obj)
            authTokenDAO theauthTokenDAO = new authTokenDAO(connection);

            // Before create the new user, first make sure that the user has not already been registered (bc each user needs be unique)
            if (theUserDAO.getUserInfo(r.getUsername()) != null) {
                // Since doesn't equal null, user has already been registered so need to close the connection with a failure result obj returned to handler
                db.closeConnection(false);
                // Want a failure result obj so only plug in the message, success boolean to use that failure constructor made in that class
                RegisterResponse result = new RegisterResponse("Error: Username already taken by another user, all usernames must be unique", false);
                return result;
            }
            // If enter here then user has not already been registered, so need actually register them now!
            else {
                // So need create User class obj so that can plug them in when create the new user
                // Don't have a getPersonID method in RegisterRequest class bc need use a random, unique string from UUID class for pass off data to work (so call the one made above)
                User theUser = new User(r.getUsername(), r.getPassword(), r.getEmail(), r.getFirstName(),
                        r.getLastName(), r.getGender(), wantedPersonID);

                // Now actually create the new user with userDAO class method
                theUserDAO.createNewUser(theUser);

                // Need generate 4 generations of ancestors when register a new user
                // So first read in all of the family tree data have
                GenerateFamilyTree data = new GenerateFamilyTree();
                data.ReadInData();

                // Then call the method from the GenerateFamilyTree class that will generate the 4 generations when register a new user!
                data.generateAncestorData(connection, theUser, 4);

                // Now actually get the authtoken class obj, takes the authtoken and the username in
                // Need create another random unique string to use for the authtoken here
                String wantedAuthtoken = UUID.randomUUID().toString();
                Authtoken theAuthtoken = new Authtoken(wantedAuthtoken, r.getUsername());

                // Actually create the authtoken for this user
                theauthTokenDAO.createNewToken(theAuthtoken);

                // Close DB connection, COMMIT transaction
                db.closeConnection(true);

                // Create and return SUCCESS Result obj
                // Success response constructor has authtoken, username, personID, success boolean (will be true here)
                // Want use the authtoken just created above as the authtoken for success reponse obj
                RegisterResponse result = new RegisterResponse(wantedAuthtoken, r.getUsername(), wantedPersonID, true);
                return result;
            }
        }
        catch (Exception e){
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            RegisterResponse result = new RegisterResponse("Error: Failed to successfully register the user", false);
            return result;
        }
    }
}
