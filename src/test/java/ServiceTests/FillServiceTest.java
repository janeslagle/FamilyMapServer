package ServiceTests;

import DAO.*;
import Model.User;
import Result.FillResponse;
import Service.FillService;
import Request.FillRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.lang.Math;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FillServiceTest {
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
    public void fillServicePass() throws DataAccessException {
        // Need input a user into the DB 1st before can do fill for it (bc user doing fill request on needs already be registered with the server)

        // So first create the user that will insert into the DB and do the fill for
        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");

        // So need to open a connection to the DB, insert user into it, all of that, etc.
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

            // Now can insert the user, person, event and authtoken into the DB so have something to clear when call clear service
            userDAO newUdao = new userDAO(connection);
            newUdao.createNewUser(theUser);

            DB.closeConnection(true);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Now do the fill service

        // For the fill service: need a username + # generations inserted as parameters
        // # generations is inputted by the user in urlpath, usually test it with numbers 0-5 I think so for this test, just generate a random int in range of 0-5
        Random rand = new Random();
        int useForGenerations = rand.nextInt(6);   // Generates rand int in range of 0 to 6-1 so to 5

        // NOW make fill request for user that want log in (for user that just inserted into the DB)
        FillRequest theFillRequest = new FillRequest(theUser.getUsername(), useForGenerations);

        // Get the expected response expect to see for filling for this user (expect success response)
        // For success response: need the number of people and number of events added to the DB
        // So need get those based on number of generations made the request with
        // SO for n generations, have 2^(n+1)-1 people and (# ppl)*3 - 2 number of events, so get those ints now
        int numPeopleShouldHave = (int) (Math.pow(2, useForGenerations+1) - 1);
        int numEventsShouldHave = (numPeopleShouldHave*3) - 2;
        FillResponse shouldWork = new FillResponse("Successfully added " + numPeopleShouldHave + " persons and " + numEventsShouldHave + " events to the database.", true);

        // Now actually do the fill service and make sure it was successful!
        FillService service = new FillService();
        FillResponse whatGetFromService = service.FillService(theFillRequest);

        // Make sure the response from the service matches the success expected response!
        assertEquals(shouldWork.getSuccess(), whatGetFromService.getSuccess());   // These should both be true
        assertEquals(shouldWork.getMessage(), whatGetFromService.getMessage());   // These should both be the success response message
    }

    @Test
    public void fillServiceFail() throws DataAccessException {
        // Know fail when try to fill for username when given a non-positive int for the generations parameter
        // So try that case first, create and insert user into DB and then try to make a fill request for them with non-pos generations and make sure it's a fail response
        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");

        // So need to open a connection to the DB, insert user into it, all of that, etc.
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

            // Now can insert the user, person, event and authtoken into the DB so have something to clear when call clear service
            userDAO newUdao = new userDAO(connection);
            newUdao.createNewUser(theUser);

            DB.closeConnection(true);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Make fill request with non-pos generations
        FillRequest theFillRequest = new FillRequest(theUser.getUsername(), -2);

        // Make the failed response that it should be
        FillResponse shouldFail = new FillResponse("Error: Number of generations was less than 0, it must be a non-negative integer", false);

        // Now actually get the fill service with the bad request to make sure it matches the fail response
        FillService service = new FillService();
        FillResponse whatGetFromService = service.FillService(theFillRequest);

        assertEquals(shouldFail.getSuccess(), whatGetFromService.getSuccess());   // These should both be false
        assertEquals(shouldFail.getMessage(), whatGetFromService.getMessage());   // This should be the non-neg generations error message for when fail

        // Also know that fail when try to fill for a user that has not been registered in the DB yet SO try with an unregistered user and make sure it gives that fail response too
        FillRequest anotherFillRequest = new FillRequest("sally", 4);
        FillResponse shouldFailToo = new FillResponse("Error: User has not been registered in the database already so cannot generate ancestors for them", false);

        FillResponse whatGetFromServiceAgain = service.FillService(anotherFillRequest);

        assertEquals(shouldFailToo.getSuccess(), whatGetFromServiceAgain.getSuccess());   // These should both be false
        assertEquals(shouldFailToo.getMessage(), whatGetFromServiceAgain.getMessage());   // These should be the user not registered already error message for when fail
    }
}
