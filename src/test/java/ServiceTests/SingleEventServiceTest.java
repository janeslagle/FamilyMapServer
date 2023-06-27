package ServiceTests;

import DAO.*;
import Model.Authtoken;
import Model.Event;
import Model.User;
import Request.SingleEventRequest;
import Result.SingleEventResponse;
import Service.SingleEventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SingleEventServiceTest {
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
    public void singleEventServicePass() throws DataAccessException {
        // Testing that returns event obj for given eventID in request SO create that event obj that will find in this request
        // Create a user first and then use that user to actually make the event (bc the event will be assigned to that user)
        User theUser = new User("jane", "password", "email","jane", "slagle", "f",
                "thisISME!");
        Event theEvent =  new Event("janeEvent", theUser.getUsername(), theUser.getPersonID(), 67.5f, 123.4f, "france",
                "paris", "whoCARES", 2453);

        // ALSO need to set an authtoken for this user bc need to plug their authtoken in when make the request
        Authtoken theAuthtoken = new Authtoken("janeToken", theEvent.getAssociatedUsername());

        // Now need to insert the user, person and authtoken into the DB so that they are in the DB when try to find them!
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

            // Now can insert the user, event and authtoken into the DB
            userDAO newUdao = new userDAO(connection);
            eventDAO newEdao = new eventDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newEdao.insert(theEvent);
            newAdao.createNewToken(theAuthtoken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Now make the single event request, need to plug the eventID and the authtoken for the event trying to find into the request
        SingleEventRequest singleEventRequest = new SingleEventRequest(theEvent.getEventID(), theAuthtoken.getAuthToken());

        // Make the single event response object that the service response should match (will be the person object itself)
        SingleEventResponse correctResponse = new SingleEventResponse(theEvent.getAssociatedUsername(), theEvent.getEventID(), theEvent.getPersonID(),
                theEvent.getLatitude(), theEvent.getLongitude(), theEvent.getCountry(), theEvent.getCity(), theEvent.getEventType(),
                theEvent.getYear(), true);

        // Now get the single event service object
        SingleEventService service = new SingleEventService();
        SingleEventResponse serviceResponse = service.SingleEventService(singleEventRequest);

        // Make sure that all elements of the service response match the success response
        assertEquals(correctResponse.getAssociatedUsername(), serviceResponse.getAssociatedUsername());
        assertEquals(correctResponse.getEventID(), serviceResponse.getEventID());
        assertEquals(correctResponse.getPersonID(), serviceResponse.getPersonID());
        assertEquals(correctResponse.getLatitude(), serviceResponse.getLatitude());
        assertEquals(correctResponse.getLongitude(), serviceResponse.getLongitude());
        assertEquals(correctResponse.getCountry(), serviceResponse.getCountry());
        assertEquals(correctResponse.getCity(), serviceResponse.getCity());
        assertEquals(correctResponse.getEventType(), serviceResponse.getEventType());
        assertEquals(correctResponse.getYear(), serviceResponse.getYear());
        assertEquals(correctResponse.getSuccess(), serviceResponse.getSuccess());

        // Make sure the service response has no elements of a failure response (only has message if it failed, so that should be null)
        assertNull(serviceResponse.getMessage());
    }

    @Test
    public void singleEventServiceFail() throws DataAccessException {
        // Times could fail with the single event request are:
        // (1) no user assocaited with authtoken (given wrong authtoken, wrong user)
        // (2) no event with eventID, so given correct authtoken but wrong eventID
        // (3) username of event doesn't match username of given authtoken

        // So go through and account for all of these
        // Create the same events / authtokens / users made for pass test
        User theUser = new User("jane", "password", "email","jane", "slagle", "f",
                "thisISME!");
        Event theEvent =  new Event("janeEvent", theUser.getUsername(), theUser.getPersonID(), 67.5f, 123.4f, "france",
                "paris", "whoCARES", 2453);

        // ALSO need to set an authtoken for this user bc need to plug their authtoken in when make the request
        Authtoken theAuthtoken = new Authtoken("janeToken", theEvent.getAssociatedUsername());

        User otherUser = new User("frankie", "password", "email","frankie", "knowles", "f",
                "thisISFRANKIE!");
        Event otherEvent =  new Event("frankieEvent", otherUser.getUsername(), otherUser.getPersonID(), 67.5f, 123.4f, "france",
                "paris", "whoCARES", 8965);

        // ALSO need to set an authtoken for this user bc need to plug their authtoken in when make the request
        Authtoken otherAuthtoken = new Authtoken("frankieToken", otherEvent.getAssociatedUsername());

        // Now need to insert the user, person and authtoken into the DB so that they are in the DB when try to find them!
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

            // Now can insert the user, event and authtoken into the DB
            userDAO newUdao = new userDAO(connection);
            eventDAO newEdao = new eventDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newEdao.insert(theEvent);
            newAdao.createNewToken(theAuthtoken);

            newUdao.createNewUser(otherUser);
            newEdao.insert(otherEvent);
            newAdao.createNewToken(otherAuthtoken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Create fail response object that will comapre these fail cases to to make sure they actually fail!
        // Go through fail case for when no user for the authtoken
        SingleEventResponse failUserResponse = new SingleEventResponse("Error: There is no user associated with the given authtoken, you gave an invalid authtoken", false);

        // For this fail case, give it wrong eventID and wrong authtoken
        SingleEventRequest failUserRequest = new SingleEventRequest("anEvent", "sdkfjsdklf");

        // Now get the single event service object for this failed case
        SingleEventService service = new SingleEventService();
        SingleEventResponse serviceResponse = service.SingleEventService(failUserRequest);

        // Make sure it matches the success bool and message for the failed case
        assertEquals(failUserResponse.getMessage(), serviceResponse.getMessage());
        assertEquals(failUserResponse.getSuccess(), serviceResponse.getSuccess());

        SingleEventResponse failIDResponse = new SingleEventResponse("Error: There is no event associated with the given eventID, you gave an invalid eventID", false);
        SingleEventRequest failIDRequest = new SingleEventRequest("idk", theAuthtoken.getAuthToken());
        SingleEventResponse responseAgain = service.SingleEventService(failIDRequest);

        assertEquals(failIDResponse.getMessage(), responseAgain.getMessage());
        assertEquals(failIDResponse.getSuccess(), responseAgain.getSuccess());

        // Case where the username of event doesn't match username of given authtoken
        // Find by use eventID username of one, authtokenID username of other for the test
        SingleEventResponse anotherFailed = new SingleEventResponse("Error: The username of the event doesn't match the username of the given authtoken, invalid request", false);
        SingleEventRequest anotherFailedRequest = new SingleEventRequest("frankieEvent", "janeToken");
        SingleEventResponse anotherFailedResponse = service.SingleEventService(anotherFailedRequest);

        assertEquals(anotherFailedResponse.getMessage(), anotherFailed.getMessage());
        assertEquals(anotherFailedResponse.getSuccess(), anotherFailed.getSuccess());
    }
}
