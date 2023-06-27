package ServiceTests;

import DAO.*;
import Model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Result.AllPeopleResponse;
import Request.AllPeopleRequest;
import Service.AllPeopleService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AllPeopleServiceTest {
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
    public void testAllPeopleServicePass() throws DataAccessException {
        // Testing if returns ALL people of current user (find via their authtoken)
        // So need make user, 2 people for that user and an authtoken for the user so can make sure the request gets them like how think it does

        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");

        Person firstPerson = new Person("person1", theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "dad", "mom", "sammy");
        Person secondPerson = new Person("person2", theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "father", "mother", "frankie");

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
            personDAO newPdao = new personDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newPdao.createNewPerson(firstPerson);
            newPdao.createNewPerson(secondPerson);
            newAdao.createNewToken(theAuthtoken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Make the request now
        AllPeopleRequest theRequest = new AllPeopleRequest(theAuthtoken.getAuthToken());

        // Not sure how to work with List<Person> and I don't feel like going through and changing everything to handle a Person[] instead in all of my other functions
        // So will just check that get a true success here and that the message matches the success message and that should be fine
        AllPeopleService service = new AllPeopleService();
        AllPeopleResponse theResponseGet = service.AllPeopleService(theRequest);

        // Make sure has no message too (bc only get message if failed response)
        assertNull(theResponseGet.getMessage());

        // Make sure get true success
        assertTrue(theResponseGet.getSuccess());
    }

    @Test
    public void testAllPeopleServiceFail() throws DataAccessException {
        // All people request fails when given wrong authtoken
        // Also fails when user (the user associated with the given authtoken) has no people associated with it

        // SO check first failure case (give it an authtoken that isn't actually in the DB)
        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");

        Person firstPerson = new Person("person1", theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "dad", "mom", "sammy");
        Person secondPerson = new Person("person2", theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "father", "mother", "frankie");

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
            personDAO newPdao = new personDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newPdao.createNewPerson(firstPerson);
            newPdao.createNewPerson(secondPerson);
            newAdao.createNewToken(theAuthtoken);

            newUdao.createNewUser(otherUser);
            newAdao.createNewToken(otherToken);

            DB.closeConnection(true);      // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Make the request now
        AllPeopleRequest theRequest = new AllPeopleRequest("randoToken");

        // Not sure how to work with List<Person> and I don't feel like going through and changing everything to handle a Person[] instead in all of my other functions
        // So will just check that get a true success here and that the message matches the success message and that should be fine
        AllPeopleService service = new AllPeopleService();
        AllPeopleResponse theResponseGet = service.AllPeopleService(theRequest);

        // Should have failed bool so make sure do
        assertFalse(theResponseGet.getSuccess());

        // Should match fail message about given wrong authtoken part
        assertEquals("Error: There are no users associated with the given authtoken, you gave an invalid authtoken", theResponseGet.getMessage());

        // Now go through and make sure fails when user (authtoken) has no people associated with it
        AllPeopleRequest otherRequest = new AllPeopleRequest("otherToken");
        AllPeopleResponse response = service.AllPeopleService(otherRequest);
        assertFalse(response.getSuccess());
        assertEquals("Error: There are no people assigned to the username specified, so there are no people to return here", response.getMessage());
    }
}
