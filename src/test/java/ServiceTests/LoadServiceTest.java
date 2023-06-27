package ServiceTests;

import DAO.*;
import Model.*;
import Request.LoadRequest;
import Result.LoadResponse;
import Service.LoadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadServiceTest {
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
    public void loadServicePass() throws DataAccessException {
        // Need create Model class objects that will make sure the load service actually adds into DB
        // So make 2 objects for each type of model class have (for users, people and events, those are what load will load into the DB)

        User firstUser = new User("jane", "password", "email", "jane", "slagle",
                "f", "123");
        User secondUser = new User("camille", "password", "email", "camille", "slagle",
                "f", "345");

        // Make the list of users that will plug in when make the load request object
        User[] theUsers = new User[2];    // initialize it to have 2 elements because we have 2 users here

        // Now actually add the users into the list just made
        theUsers[0] = firstUser;
        theUsers[1] = secondUser;

        Person firstPerson = new Person(firstUser.getPersonID(), firstUser.getUsername(), firstUser.getFirstName(), firstUser.getLastName(),
                firstUser.getGender(), "dad", "mom", "sammy");
        Person secondPerson = new Person(secondUser.getPersonID(), secondUser.getUsername(), secondUser.getFirstName(), secondUser.getLastName(),
                secondUser.getGender(), "father", "mother", "tom" );

        // Make the list of people that will plug in when make the load request
        Person[] thePersons = new Person[2];   // Will add two people into the list

        // Now actually add the people into the list just made
        thePersons[0] = firstPerson;
        thePersons[1] = secondPerson;

        Event firstEvent = new Event("fjsdjfsd", firstUser.getUsername(), firstUser.getPersonID(), 56.5f, 124.2f,
                "france", "paris", "whocares", 1567);
        Event secondEvent = new Event("wlkwjrklsdjf", secondUser.getUsername(), secondUser.getPersonID(), 67.5f, 987.4f, "spain",
                "madrid", "whocaresAgain", 1432);

        // Make the list of events that will plug in when make the load request
        Event[] theEvents = new Event[2];    // Will add 2 events into the list

        // Now actually add the events into the list just made
        theEvents[0] = firstEvent;
        theEvents[1] = secondEvent;

        // NOW make the load request object
        LoadRequest theLoadRequest = new LoadRequest(theUsers, thePersons, theEvents);

        // Now make the response object for what expect to see if the load is successful so that can compare what actually get from the service to it
        // For success reponse though, need the number of events, people and users just added into the database (added 2 of each so just hardcode it in)
        LoadResponse successResponse = new LoadResponse("Successfully added 2 users, 2 persons, and 2 events to the database.", true);

        // Now actually do the load service
        LoadService service = new LoadService();
        LoadResponse whatGetFromService = service.LoadService(theLoadRequest);

        assertEquals(whatGetFromService.getSuccess(), successResponse.getSuccess());     // These should both be true
        assertEquals(whatGetFromService.getMessage(), successResponse.getMessage());    // These should both be the success response
    }

    @Test
    public void loadServiceFail() throws DataAccessException {
        // Know fail when one of the inputted Users, Persons or Events lists are empty SO try all of the combos for having which one is empty and make sure get failed response

        // So first try to make the load request with ALL empty lists and make sure it fails, make them empty by setting them as null
        LoadRequest emptyLoadRequest = new LoadRequest(null, null, null);

        // Make the failure response object that the response from service with this request should match!
        LoadResponse shouldFail = new LoadResponse("Error: Either one, two or all of the Users, Persons or Events lists inputted in the request body were empty", false);

        // Now actually do the service, makes sure matches the failed response
        LoadService service = new LoadService();
        LoadResponse whatGetFromEmptyRequest = service.LoadService(emptyLoadRequest);

        assertEquals(shouldFail.getSuccess(), whatGetFromEmptyRequest.getSuccess());   // These should both be false
        assertEquals(shouldFail.getMessage(), whatGetFromEmptyRequest.getMessage());   // These should both be message about the lists being empty

        // Now make sure fails when the users list is empty, but have the persons and events
        // So first define the users, persons, and events lists that used before

        User firstUser = new User("jane", "password", "email", "jane", "slagle",
                "f", "123");
        User secondUser = new User("camille", "password", "email", "camille", "slagle",
                "f", "345");

        // Make the list of users that will plug in when make the load request object
        User[] theUsers = new User[2];    // initialize it to have 2 elements because we have 2 users here

        // Now actually add the users into the list just made
        theUsers[0] = firstUser;
        theUsers[1] = secondUser;

        Person firstPerson = new Person(firstUser.getPersonID(), firstUser.getUsername(), firstUser.getFirstName(), firstUser.getLastName(),
                firstUser.getGender(), "dad", "mom", "sammy");
        Person secondPerson = new Person(secondUser.getPersonID(), secondUser.getUsername(), secondUser.getFirstName(), secondUser.getLastName(),
                secondUser.getGender(), "father", "mother", "tom" );

        // Make the list of people that will plug in when make the load request
        Person[] thePersons = new Person[2];    // Will load 2 people into the list

        // Now actually add the people into the list just made
        thePersons[0] = firstPerson;
        thePersons[1] = secondPerson;

        Event firstEvent = new Event("fjsdjfsd", firstUser.getUsername(), firstUser.getPersonID(), 56.5f, 124.2f,
                "france", "paris", "whocares", 1567);
        Event secondEvent = new Event("wlkwjrklsdjf", secondUser.getUsername(), secondUser.getPersonID(), 67.5f, 987.4f, "spain",
                "madrid", "whocaresAgain", 1432);

        // Make the list of events that will plug in when make the load request
        Event[] theEvents = new Event[2];      // Will load 2 events into the list

        // Now actually add the events into the list just made
        theEvents[0] = firstEvent;
        theEvents[1] = secondEvent;

        LoadRequest emptyUsersRequest = new LoadRequest(null, thePersons, theEvents);
        LoadResponse emptyUsersResponse = service.LoadService(emptyUsersRequest);
        assertEquals(shouldFail.getSuccess(), emptyUsersResponse.getSuccess());   // These should both be false
        assertEquals(shouldFail.getMessage(), emptyUsersResponse.getMessage());   // These should both be error message about empty lists

        // Now test for empty persons list
        LoadRequest emptyPersonsRequest = new LoadRequest(theUsers, null, theEvents);
        LoadResponse emptyPersonsResponse = service.LoadService(emptyPersonsRequest);
        assertEquals(shouldFail.getSuccess(), emptyPersonsResponse.getSuccess());   // These should both be false
        assertEquals(shouldFail.getMessage(), emptyPersonsResponse.getMessage());   // These should both be error message about empty lists

        // Now test for empty events list
        LoadRequest emptyEventsRequest = new LoadRequest(theUsers, thePersons, null);
        LoadResponse emptyEventsResponse = service.LoadService(emptyEventsRequest);
        assertEquals(shouldFail.getSuccess(), emptyEventsResponse.getSuccess());   // These should both be false
        assertEquals(shouldFail.getMessage(), emptyEventsResponse.getMessage());   // These should both be error message about empty lists
    }
}
