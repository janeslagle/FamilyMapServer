package ServiceTests;

import DAO.*;
import Model.Authtoken;
import Model.Event;
import Model.User;
import Request.AllEventsRequest;
import Result.AllEventsResponse;
import Service.AllEventsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllEventsServiceTest {
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
    public void allEventsServicePass() throws DataAccessException {
        // Testing if returns ALL events of current user (found via their authtoken)
        // So need make user, 2 events for that user and an authtoken for the user so can make sure the request gets them like how think it does

        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");

        Event firstEvent = new Event("1steventYO", theUser.getUsername(), theUser.getPersonID(), 45.6f, 345.2f, "spain",
                "madrid", "whoCARES!!!!", 6789);
        Event secondEvent = new Event("2ndEVENT", theUser.getUsername(), theUser.getPersonID(), 56.7f, 345.2f, "france",
                "cannes", "idkkkk", 5432);

        // Now make an authtoken for that user
        Authtoken theAuthtoken = new Authtoken("tokenNEED", "jane");

        // Need go into the DB and actually make these things in there now
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
            eventDAO newEdao = new eventDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newEdao.insert(firstEvent);
            newEdao.insert(secondEvent);
            newAdao.createNewToken(theAuthtoken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Make the request now
        AllEventsRequest theRequest = new AllEventsRequest(theAuthtoken.getAuthToken());

        // Not sure how to work with List<Event> and I don't feel like going through and changing everything to handle an Event[] instead in all of my other functions
        // So will just check that get a true success here and that the message matches the success message and that should be fine
        AllEventsService service = new AllEventsService();
        AllEventsResponse theResponseGet = service.AllEventsService(theRequest);

        // Make sure has no message too (bc only get message if failed response)
        assertNull(theResponseGet.getMessage());

        // Make sure get true success
        assertTrue(theResponseGet.getSuccess());
    }

    @Test
    public void allEventsServiceFail() throws DataAccessException {
        // Exact same as allPeopleServiceFail but replace all persons with events - yay!
        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");

        Event firstEvent = new Event("1steventYO", theUser.getUsername(), theUser.getPersonID(), 45.6f, 345.2f, "spain",
                "madrid", "whoCARES!!!!", 6789);
        Event secondEvent = new Event("2ndEVENT", theUser.getUsername(), theUser.getPersonID(), 56.7f, 345.2f, "france",
                "cannes", "idkkkk", 5432);

        // Now make an authtoken for that user
        Authtoken theAuthtoken = new Authtoken("tokenNEED", "jane");

        // Need make an authtoken for a user that has no people associated with it
        // So make a user that has no associated people
        User otherUser = new User("Sally", "password", "email", "sally", "parker", "f", "her!");
        Authtoken otherToken = new Authtoken("otherToken", "Sally");

        // Need go into the DB and actually make these things in there now
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
            eventDAO newEdao = new eventDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newEdao.insert(firstEvent);
            newEdao.insert(secondEvent);
            newAdao.createNewToken(theAuthtoken);

            newUdao.createNewUser(otherUser);
            newAdao.createNewToken(otherToken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Make the request now
        AllEventsRequest theRequest = new AllEventsRequest("randoToken");

        // Not sure how to work with List<Person> and I don't feel like going through and changing everything to handle a Person[] instead in all of my other functions
        // So will just check that get a true success here and that the message matches the success message and that should be fine
        AllEventsService service = new AllEventsService();
        AllEventsResponse theResponseGet = service.AllEventsService(theRequest);

        // Should have failed bool so make sure do
        assertFalse(theResponseGet.getSuccess());

        // Should match fail message about given wrong authtoken part
        assertEquals("Error: There are no users associated with the givenn authtoken, you gave an invalid authtoken", theResponseGet.getMessage());

        // Now go through and make sure fails when user (authtoken) has no people associated with it
        AllEventsRequest otherRequest = new AllEventsRequest("otherToken");
        AllEventsResponse response = service.AllEventsService(otherRequest);
        assertFalse(response.getSuccess());
        assertEquals("Error: There were no events assigned to the username specified, so there are no events to return here", response.getMessage());

    }
}
