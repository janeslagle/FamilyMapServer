package Service;

import DAO.*;
import Model.Authtoken;
import Model.User;
import Request.LoginRequest;
import Result.LoginResponse;

import java.sql.Connection;
import java.util.UUID;

/**
 * This class actually does what the client requests, performs functions of server for real!
 * There is a different service class for each request function
 * This class corresponds to the request function of logging a new user in the DB, to the LoginRequest class
 */
public class LoginService {
    /**
     * Actually perform the login request now
     *
     * @param r The request of logging a new user in
     * @return The result from performing new login of user
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public LoginResponse LoginService(LoginRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            // Open DB connection and store it so that can use it to make userDAO class obj
            Connection connection = db.openConnection();

            // Create userDAO class obj so that can make user, etc. (want log a user in here)
            userDAO theUserDAO = new userDAO(connection);

            // Create authTokebDAO class obj (will need return an authtoken so will need this obj)
            authTokenDAO theauthTokenDAO = new authTokenDAO(connection);

            // To actually carry out the login request, need validate that the user's username + password combo actually exist
            if (theUserDAO.validateLogin(r.getUsername(), r.getPassword()) != true) {
                // Since doesn't equal true, the username + password combo aren't valid so can't actually log them in, need return failure response
                db.closeConnection(false);
                // Want a failure result obj so only plug in the message, success boolean to use that failure constructor made in that class
                LoginResponse result = new LoginResponse("Error: Given an invalid username and password combo", false);
                return result;
            }
            // If enter here then the username + password combo is valid so create success response!
            else {
                // Need make user obj bc need use the same personID for the user when log them in
                User user = theUserDAO.getUserInfo(r.getUsername());  // find the user via the userDAO method have
                String wantedPersonID = user.getPersonID();

                // Need have same authtoken as the user that are logging in as well
                String wantedAuthtoken = UUID.randomUUID().toString();
                Authtoken theAuthtoken = new Authtoken(wantedAuthtoken, r.getUsername());
                theauthTokenDAO.createNewToken(theAuthtoken);

                // Create success response
                db.closeConnection(true);

                // Need use the same personID and authtoken for the user that are logging in
                LoginResponse result = new LoginResponse(wantedAuthtoken, r.getUsername(), wantedPersonID, true);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();

            // Close DB connection, ROLLBACK transaction
            db.closeConnection(false);

            // Create, return FAILURE Result obj so use failure constructor have
            LoginResponse result = new LoginResponse("Error: Failed to successfully login the user", false);
            return result;
        }
    }
}
