package ServiceTests;

import DAO.*;
import Model.User;
import Service.LoginService;
import Request.LoginRequest;
import Result.LoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private Database db;
    private userDAO uDao;
    private eventDAO eDao;
    private personDAO pDao;
    private authTokenDAO aDao;

    // Should have the same set up and tear down as Register Service tests so just copy those over!
    @BeforeEach
    public void setUp() throws DataAccessException {
        // Create a new instance of the Database class
        db = new Database();
        Connection connection = db.getConnection();

        // Clear all users, events, persons and tokens tables each time test a new method in RegisterService
        uDao = new userDAO(connection);
        uDao.clear();

        eDao = new eventDAO(connection);
        eDao.clear();

        pDao = new personDAO(connection);
        pDao.clear();

        aDao = new authTokenDAO(connection);
        aDao.clear();

        // Need do this or it says the DB is locked when try run these tests... idk
        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() {
        // Reset the db before go into next testing function for these tests
        db = null;
    }

    // Should be really similar to register service pass test
    @Test
    public void loginServicePass() throws DataAccessException {
        // Need to insert a user into the DB before can log that user in (otherwise will not work because how can log someone in when that person doesn't even exist yet)
        User userWantLogIn = new User("JANE", "rememberMe84", "daEmail", "jane", "slagle",
                "f", "hereIAM!!!");

        // Now need to insert the user into our User table in the DB (put in try catch block bc could have DataAccessExceptions...)
        try {
            // So need to open the DB in here
            Database DB = new Database();
            Connection connection = DB.getConnection();  // Need establish a new connection to the DB

            // Clear all tables in DB just in case (they should be clear anyway from the set up func but who knows)
            uDao = new userDAO(connection);
            eDao = new eventDAO(connection);
            pDao = new personDAO(connection);
            aDao = new authTokenDAO(connection);

            uDao.clear();
            eDao.clear();
            pDao.clear();
            aDao.clear();

            // Now can insert the user want to login into the DB
            userDAO newUdao = new userDAO(connection);
            newUdao.createNewUser(userWantLogIn);    // Insert the user into our user table so that the DB / server can find it when try to log that user in!

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // NOW can make a login request for the user that just inserted into the DB
        LoginRequest theLoginRequest = new LoginRequest(userWantLogIn.getUsername(), userWantLogIn.getPassword());

        // Get the expected response that expect to get out from logging the user in that just made in the request
        // Will compare this to the response actually get from the service class to make sure the service class gives a success response
        LoginResponse whatShouldGet = new LoginResponse("theAuthtoken", userWantLogIn.getUsername(), userWantLogIn.getPersonID(), true);

        // Now get the actual response from the service object from plugging the request into it
        LoginService service = new LoginService();
        LoginResponse whatGetFromService = service.LoginService(theLoginRequest);

        // Make sure that the request with the service had a successful response
        // If successful then both the whatShouldGet and whatGetFromService should have the same success bools and usernames
        assertEquals(whatGetFromService.getSuccess(), whatShouldGet.getSuccess());     // These should both be true
        assertEquals(whatGetFromService.getUsername(), whatShouldGet.getUsername());   // These should both be jeslagle

        // The response actually get from service should have an authtoken associated with it (bc authtokens are created with successful responses)
        assertNotNull(whatGetFromService.getAuthToken());

        // The response actually get from service should not have a message because only failure responses have messages in them
        assertNull(whatGetFromService.getMessage());

        // The response actuallly get from the service should have a true success bool
        assertTrue(whatGetFromService.getSuccess());
    }

    @Test
    public void loginServiceFail() throws DataAccessException {
        // Know fail when try login a user that have no username + password combo for so try log in a user that don't have inserted into the DB already to test this

        // So insert a user into the DB again and then try to log in a user that is not the user just inserted into the DB
        // Need to insert a user into the DB before can log that user in (otherwise will not work because how can log someone in when that person doesn't even exist yet)
        User userWantLogIn = new User("JANE", "rememberMe84", "daEmail", "jane", "slagle",
                "f", "hereIAM!!!");

        // Now need to insert the user into our User table in the DB (put in try catch block bc could have DataAccessExceptions...)
        try {
            // So need to open the DB in here
            Database DB = new Database();
            Connection connection = DB.getConnection();  // Need establish a new connection to the DB

            // Clear all tables in DB just in case (they should be clear anyway from the set up func but who knows)
            uDao = new userDAO(connection);
            eDao = new eventDAO(connection);
            pDao = new personDAO(connection);
            aDao = new authTokenDAO(connection);

            uDao.clear();
            eDao.clear();
            pDao.clear();
            aDao.clear();

            // Now can insert the user want to login into the DB
            userDAO newUdao = new userDAO(connection);
            newUdao.createNewUser(userWantLogIn);   // Insert the user into our user table so that the DB / server can find it when try to log that user in!

            DB.closeConnection(true);        // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // NOW make login request for user that is NOT user just inserted into DB
        LoginRequest theLoginRequest = new LoginRequest("Sally", "sallyPassword");

        // Get the expected response that expect to get out from logging the user in that just made in the request (expect it to fail)
        // Will compare this to the response actually get from the service class to make sure the service class gives a failure response
        LoginResponse shouldFail = new LoginResponse("Error: Given an invalid username and password combo", false);

        // Now get the actual response from the service object from plugging the request into it
        LoginService service = new LoginService();
        LoginResponse whatGetFromService = service.LoginService(theLoginRequest);

        // Should be failure now so compare it to failure response to make sure it is a failure response from logging in invalid user
        assertEquals(shouldFail.getSuccess(), whatGetFromService.getSuccess());   // These should both be false
        assertEquals(shouldFail.getMessage(), whatGetFromService.getMessage());   // These should both be the failure response message

        // The response actually get from logging in invalid user should not have an authtoken bc authtokens only made with successful responses
        assertNull(shouldFail.getAuthToken());

        // They also should not have a username or personID (only have those with successful responses)
        assertNull(shouldFail.getUsername());
        assertNull(shouldFail.getPersonID());

        // The response actually get from logging in invalid user should have a message in it because it is a failure response
        assertNotNull(shouldFail.getMessage());

        // The resonse acutally get from logging in invalid user should have a false success bool
        assertFalse(shouldFail.getSuccess());
    }
}
