package ServiceTests;

import DAO.*;
import Model.*;
import Service.ClearService;
import Result.ClearResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
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
    public void testClearService() throws DataAccessException {
        // Insert multiple users, events, people and authtokens into the DB (because want to test if the clear service actually clears them from the DB)

        // Create all of the objects to insert into the DB
        User theUser = new User("jane", "password", "daemail", "Jane", "Slagle", "f", "daperson");
        User anotherUser = new User("boden", "password", "email", "Boden", "Slagle",
                "m", "person");
        User thirdUser = new User("tracey", "password", "heremail", "Tracey", "Trinh",
                "f", "herperson");

        Person thePerson = new Person(theUser.getPersonID(), theUser.getUsername(), theUser.getFirstName(), theUser.getLastName(),
                theUser.getGender(), "thefather", "themother", "thespouse");
        Person anotherPerson = new Person(anotherUser.getPersonID(), anotherUser.getUsername(), anotherUser.getFirstName(),
                anotherUser.getLastName(), anotherUser.getGender(),"father", "mother", thirdUser.getPersonID());
        Person thirdPerson = new Person(thirdUser.getPersonID(), thirdUser.getUsername(), thirdUser.getFirstName(), thirdUser.getLastName(),
                thirdUser.getGender(), "dad", "mom", anotherPerson.getPersonID());

        Event theEvent = new Event("theevent", theUser.getUsername(), thePerson.getPersonID(), 56.4f, 124.5f, "france",
                "bordeaux", "death", 2000);
        Event userBirth = new Event("2343", theUser.getUsername(), thePerson.getPersonID(), 56.7f, 678.3f,
                "america", "seattle", "birth", 1945);
        Event anotherUserBirth = new Event("456456ssd", anotherUser.getUsername(), anotherPerson.getPersonID(), 56.4f, 345.2f,
                "germany", "berlin", "birth", 1345);
        Event anotherUserEvent = new Event("55645654", anotherUser.getUsername(), anotherPerson.getPersonID(), 67.8f, 987.5f,
                "spain", "barcelona", "wentonawalk", 1356);

        Authtoken theAuthtoken = new Authtoken("thetoken", theUser.getUsername());
        Authtoken anotherToken = new Authtoken("token", anotherUser.getUsername());
        Authtoken thirdToken = new Authtoken("it", thirdUser.getUsername());

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
            personDAO newPdao = new personDAO(connection);
            eventDAO newEdao = new eventDAO(connection);
            authTokenDAO newAdao = new authTokenDAO(connection);

            newUdao.createNewUser(theUser);
            newUdao.createNewUser(anotherUser);
            newUdao.createNewUser(thirdUser);
            newPdao.createNewPerson(thePerson);
            newPdao.createNewPerson(anotherPerson);
            newPdao.createNewPerson(thirdPerson);
            newEdao.insert(theEvent);
            newEdao.insert(userBirth);
            newEdao.insert(anotherUserBirth);
            newEdao.insert(anotherUserEvent);
            newAdao.createNewToken(theAuthtoken);
            newAdao.createNewToken(anotherToken);
            newAdao.createNewToken(thirdToken);

            DB.closeConnection(true);        // Close that connection have open to the DB otherwise things will get messed up!
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Now get the response that expect to see when call the clear service (should be a success response)
        ClearResponse whatShouldGet = new ClearResponse("Clear succeeded.", true);

        ClearService service = new ClearService();
        ClearResponse whatGetFromService = service.ClearService();

        assertEquals(whatShouldGet.getSuccess(), whatGetFromService.getSuccess());   // These should both be true
        assertEquals(whatShouldGet.getMessage(), whatGetFromService.getMessage());   // These should both be the success response message

        // Double check get success response even though just checked it, but I want to do this still
        assertNotEquals("Error: Failed to clear the tables stored in the database", whatGetFromService.getMessage());
    }
}
