package ServiceTests;

import DAO.*;
import Model.*;
import Request.SinglePersonRequest;
import Result.SinglePersonResponse;
import Service.SinglePersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class SinglePersonServiceTest {
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
    public void singlePersonServicePass() throws DataAccessException {
        // Testing that returns person obj for given personID in request SO create that person obj that will find in this request
        // Create a user first and then use that user to actually make the person
        User theUser = new User("jane", "password", "email","jane", "slagle", "f",
                "thisISME!");
        Person thePerson = new Person(theUser.getPersonID(), theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "Tracy", "Nick", "Pablo");

        // ALSO need to set an authtoken for this user bc need to plug their authtoken in when make the request
        Authtoken theAuthtoken = new Authtoken("janeToken", thePerson.getAssociatedUsername());

        // Add another person into the DB too to make sure that it actually gets the personID specified and not just the 1 person in there if there's only 1 person in there
        User otherUser = new User("sally", "password", "email", "sally", "parker", "f",
                "thisisSALLY!");
        Person otherPerson = new Person(otherUser.getPersonID(), otherUser.getUsername(), otherUser.getFirstName(), otherUser.getLastName(),
                otherUser.getGender(), "Sammy", "Sam", "Sal");

        // Need to make an authtoken for the other person too
        Authtoken otherAuthtoken = new Authtoken("sallyToken", otherPerson.getAssociatedUsername());

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

            // Now can insert the user, person and authtoken into the DB
            userDAO newUdao = new userDAO(connection);
            personDAO newPdao = new personDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newPdao.createNewPerson(thePerson);
            newAdao.createNewToken(theAuthtoken);

            // Create them for the other person too
            newUdao.createNewUser(otherUser);
            newPdao.createNewPerson(otherPerson);
            newAdao.createNewToken(otherAuthtoken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Now make the single person request, need to plug the personID and the authtoken for the person trying to find into the request
        SinglePersonRequest singlePersonRequest = new SinglePersonRequest(thePerson.getPersonID(), theAuthtoken.getAuthToken());

        // Make the single person response object that the service response should match (will be the person object itself)
        SinglePersonResponse correctResponse = new SinglePersonResponse(thePerson.getAssociatedUsername(), thePerson.getPersonID(), thePerson.getFirstName(),
                thePerson.getLastName(), thePerson.getGender(), thePerson.getFatherID(), thePerson.getMotherID(), thePerson.getSpouseID(), true);

        // Now get the single person service object
        SinglePersonService service = new SinglePersonService();
        SinglePersonResponse serviceResponse = service.SinglePersonService(singlePersonRequest);

        // Make sure that all elements of the service response match the success response
        assertEquals(correctResponse.getPersonID(), serviceResponse.getPersonID());
        assertEquals(correctResponse.getAssociatedUsername(), serviceResponse.getAssociatedUsername());
        assertEquals(correctResponse.getAssociatedUsername(), serviceResponse.getAssociatedUsername());
        assertEquals(correctResponse.getFirstName(), serviceResponse.getFirstName());
        assertEquals(correctResponse.getLastName(), serviceResponse.getLastName());
        assertEquals(correctResponse.getGender(), serviceResponse.getGender());
        assertEquals(correctResponse.getFatherID(), serviceResponse.getFatherID());
        assertEquals(correctResponse.getMotherID(), serviceResponse.getMotherID());
        assertEquals(correctResponse.getSpouseID(), serviceResponse.getSpouseID());

        // Make sure the service response has no elements of a failure response (only has message if it failed, so that should be null)
        assertNull(serviceResponse.getMessage());

        // Make sure that the personID of the service response object doesn't equal the personID for sally
        assertNotEquals(serviceResponse.getPersonID(), otherPerson.getPersonID());
    }

    @Test
    public void singlePersonServiceFail() throws DataAccessException {
        // Times could fail with the single person request are:
        // (1) invalid authtoken (no user in DB assigned for the authtoken given)
        // (2) wrong personID (no person in DB with that personID)

        // So go through and account for all of these
        // Create the same people / authtokens / users made for pass test

        User theUser = new User("jane", "password", "email","jane", "slagle", "f",
                "thisISME!");
        Person thePerson = new Person(theUser.getPersonID(), theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "Tracy", "Nick", "Pablo");

        // ALSO need to set an authtoken for this user bc need to plug their authtoken in when make the request
        Authtoken theAuthtoken = new Authtoken("janeToken", thePerson.getAssociatedUsername());

        // Add another person into the DB too to make sure that it actually gets the personID specified and not just the 1 person in there if there's only 1 person in there
        User otherUser = new User("sally", "password", "email", "sally", "parker", "f",
                "thisisSALLY!");
        Person otherPerson = new Person(otherUser.getPersonID(), otherUser.getUsername(), otherUser.getFirstName(), otherUser.getLastName(),
                otherUser.getGender(), "Sammy", "Sam", "Sal");

        // Need to make an authtoken for the other person too
        Authtoken otherAuthtoken = new Authtoken("sallyToken", otherPerson.getAssociatedUsername());

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

            // Now can insert the user, person and authtoken into the DB
            userDAO newUdao = new userDAO(connection);
            personDAO newPdao = new personDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newPdao.createNewPerson(thePerson);
            newAdao.createNewToken(theAuthtoken);

            // Create them for the other person too
            newUdao.createNewUser(otherUser);
            newPdao.createNewPerson(otherPerson);
            newAdao.createNewToken(otherAuthtoken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Create fail response object that will comapre these fail cases to to make sure they actually fail!
        // Go through fail case for when no user for the authtoken
        SinglePersonResponse failUserResponse = new SinglePersonResponse("Error: There is no user associated with the given authtoken, you gave an invalid authtoken", false);

        // For this fail case, give it wrong personID and wrong authtoken
        SinglePersonRequest failUserRequest = new SinglePersonRequest("tommy", "sdkfjsdklf");

        // Now get the single person service object for this failed case
        SinglePersonService service = new SinglePersonService();
        SinglePersonResponse serviceResponse = service.SinglePersonService(failUserRequest);

        // Make sure it matches the success bool and mesage for the failed case
        assertEquals(failUserResponse.getMessage(), serviceResponse.getMessage());
        assertEquals(failUserResponse.getSuccess(), serviceResponse.getSuccess());

        SinglePersonResponse failIDResponse = new SinglePersonResponse("Error: There is no person associated with the given personID, you gan an invalid personID", false);
        SinglePersonRequest failIDRequest = new SinglePersonRequest("tommy", theAuthtoken.getAuthToken());
        SinglePersonResponse responseAgain = service.SinglePersonService(failIDRequest);

        assertEquals(failIDResponse.getMessage(), responseAgain.getMessage());
        assertEquals(failIDResponse.getSuccess(), responseAgain.getSuccess());

        // Case where username of personID doesnt match username of authtokenID
        SinglePersonResponse anotherFailed = new SinglePersonResponse("Error: The username of the person associated with the inputted personID doesn't match the username associated with the given authtoken, invalid request", false);
        SinglePersonRequest anotherFailedRequest = new SinglePersonRequest("thisisSALLY!", "janeToken");
        SinglePersonResponse anotherFailedResponse = service.SinglePersonService(anotherFailedRequest);

        assertEquals(anotherFailedResponse.getMessage(), anotherFailed.getMessage());
        assertEquals(anotherFailedResponse.getSuccess(), anotherFailed.getSuccess());
    }
}
