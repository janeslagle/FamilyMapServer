package ServiceTests;

import DAO.*;
import Service.RegisterService;
import Request.RegisterRequest;
import Result.RegisterResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private Database db;
    private userDAO uDao;
    private eventDAO eDao;
    private personDAO pDao;
    private authTokenDAO aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Create a new instance of the Database class
        db = new Database();
        Connection connection = db.getConnection();

        // Clear all users, events, persons and tokens tables each time test a new method in RegisterService
        // Need to clear ALL tables because when go through register service, generate 4 default generations for user so need to clear all of those generations
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

    @Test
    public void registerServicePass() throws DataAccessException {
        // First make a register request for the user want to register
        RegisterRequest theRegisterRequest = new RegisterRequest("jeslagle", "janeRocks3!","janeslagle@hotmail.com",
                "Jane", "Slagle", "f");

        // Get the expected response that expect to get out registering the user just made in the request
        // Will compare this to the response actually get from the service class to make sure the service class gives a success response
        RegisterResponse whatShouldGet = new RegisterResponse("theAuthtoken", "jeslagle", "janeee333", true);

        // Now get the actual response from the service object from plugging the request into it
        RegisterService service = new RegisterService();
        RegisterResponse whatGetFromService = service.RegisterService(theRegisterRequest);

        // Make sure that the request with the service had a successful response
        // If successful then both the whatShouldGet and whatGetFromService should have the same success bools and usernames
        assertEquals(whatGetFromService.getSuccess(), whatShouldGet.getSuccess());     // These should both be true
        assertEquals(whatGetFromService.getUsername(), whatShouldGet.getUsername());   // These should both be jeslagle

        // The response actually get from service should have an authtoken associated with it (bc authtokens are created with successful responses)
        assertNotNull(whatGetFromService.getAuthtoken());

        // The response actually get from service should not have a message because only failure responses have messages in them
        assertNull(whatGetFromService.getMessage());

        // The response actuallly get from the service should have a true success bool
        assertTrue(whatGetFromService.getSuccess());
    }

    @Test
    public void registerServiceFail() throws DataAccessException {
        // Know failed if register a user that has already been registered so test that here, make sure actually get a failure response with this

        // The user that want to register
        RegisterRequest theRegisterRequest = new RegisterRequest("jeslagle", "janeRocks3!","janeslagle@hotmail.com",
                "Jane", "Slagle", "f");

        // Create a failure response object that can use to make sure the response get from the service is actually a failure response too
        RegisterResponse shouldBeFailure = new RegisterResponse("Error: Username already taken by another user, all usernames must be unique", false);

        // Register the user in the request
        RegisterService service = new RegisterService();
        RegisterResponse whatGetFromService = service.RegisterService(theRegisterRequest);

        // Create another user with the same username as the user just registered so can make sure it fails when do this
        RegisterRequest anotherRequest = new RegisterRequest("jeslagle", "anotherPassword", "daEmail",
                "sally", "parker", "f");
        RegisterResponse registerSameUser = service.RegisterService(anotherRequest);  // Get the response from registering them twice

        // Should be failure now (the registerSameUser) so compare it to failure response to make sure it is a failure response from registering them 2x
        assertEquals(shouldBeFailure.getSuccess(), registerSameUser.getSuccess());    // These should both be false
        assertEquals(shouldBeFailure.getMessage(), registerSameUser.getMessage());    // These should both be the failure response message

        // The response actually get from registering user twice should not have an authtoken bc authtokens only made with successful responses
        assertNull(shouldBeFailure.getAuthtoken());

        // They also should not have a username or personID (only have those with successful responses)
        assertNull(shouldBeFailure.getUsername());
        assertNull(shouldBeFailure.getPersonID());

        // The response actually get from registering user twice should have a message in it because it is a failure response
        assertNotNull(shouldBeFailure.getMessage());

        // The resonse acutally get from registering user twice should have a false success bool
        assertFalse(shouldBeFailure.getSuccess());
    }

}
