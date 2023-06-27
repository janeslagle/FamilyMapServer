package DAOTests;

import Model.Event;
import DAO.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class eventDAOTest {
    private Database db;
    private Event bestEvent;
    private Event anotherBestEvent;
    private Event forAnotherUser;
    private Event galeBirthEvent;
    private Event galeDeathEvent;
    private eventDAO eDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new event with random data
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);

        anotherBestEvent = new Event("Biking_123B", "Gale", "Gale123A",
            89.5f, 130.2f, "America", "Seattle", "Biking_Around_Again", 2008);

        forAnotherUser = new Event("Something_Cool", "Jane", "Jane333",
                78.6f, 124.7f, "Spain", "Madrid", "Having_fun", 2003);

        galeBirthEvent = new Event("gale_birth", "Gale", "Gale123A",
                89.6f, 234.4f, "Scotland", "Edinburgh", "birth", 2000);

        galeDeathEvent = new Event("gale_death", "Gale", "Gale123A",
                56.8f, 123.5f, "France", "Paris", "death", 2050);

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the EventDAO, so it can access the database.
        eDao = new eventDAO(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        eDao.clear();
    }

    @AfterEach
    public void tearDown() {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        // Start by inserting an event into the database.
        eDao.insert(bestEvent);
        // Let's use a find method to get the event that we just put in back out.
        Event compareTest = eDao.find(bestEvent.getEventID());
        // First lets see if our find method found anything at all. If it did then we know that we got
        // something back from our database.
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is the same as what we got out. If this
        // passes then we know that our insert did put something in, and that it didn't change the
        // data in any way.
        // This assertion works by calling the equals method in the Event class.
        assertEquals(bestEvent, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        // Let's do this test again, but this time lets try to make it fail.
        // If we call the method the first time the event will be inserted successfully.
        eDao.insert(bestEvent);

        // However, our sql table is set up so that the column "eventID" must be unique, so trying to insert
        // the same event again will cause the insert method to throw an exception, and we can verify this
        // behavior by using the assertThrows assertion as shown below.

        // Note: This call uses a lambda function. A lambda function runs the code that comes after
        // the "()->", and the assertThrows assertion expects the code that ran to throw an
        // instance of the class in the first parameter, which in this case is a DataAccessException.
        assertThrows(DataAccessException.class, () -> eDao.insert(bestEvent));
    }

    // Start with a clean slate / clean table for each test
    // So first test the find, it should come back as null because we didn't create the event first
    // Then actually create the event, find it and then check that it actually found the event and wasn't null (that find function works)
    @Test
    public void findPass() throws DataAccessException {
        Event findEvent = eDao.find(bestEvent.getEventID());
        assertNull(findEvent);

        eDao.insert(bestEvent);
        findEvent = eDao.find(bestEvent.getEventID());
        assertNotNull(findEvent);
    }

    // Test that it fails by finding wrong event from one that are actually testing with (so it should return null)
    @Test
    public void findFail() throws DataAccessException {
        // first need to create the event
        eDao.insert(bestEvent);
        Event findEvent = eDao.find("333");     // best event has an eventID of Biking_123A not 333 so this should make it fail
        assertNull(findEvent);                         // this should be null since put in the incorrect eventID so it shouldnt find it
    }

    @Test
    public void findAllUsersEventsPass() throws DataAccessException {
        // First try to find all of the events before create them to make sure it gives you null!
        Event findBestEvent = eDao.find(bestEvent.getEventID());
        Event findAnotherBestEvent = eDao.find(anotherBestEvent.getEventID());
        Event findForAnotherUser = eDao.find(forAnotherUser.getEventID());
        assertNull(findBestEvent);
        assertNull(findAnotherBestEvent);
        assertNull(findForAnotherUser);

        // Now actually create all events have and make sure they were added!
        eDao.insert(bestEvent);            // 1st event for Gale
        eDao.insert(anotherBestEvent);     // 2nd event for Gale
        eDao.insert(forAnotherUser);       // Event for Jane, make it so that can make sure it doesn't add that one too, only want add events for Gale!!!

        // Now find all events for user Gale and make sure it isn't null! (bc they should be added now!)
        // Need be stored in a list of events so make that
        List<Event> shouldBeGalesEvents = eDao.findAllUsersEvents("Gale");

        // Make sure that shouldBeGalesEvents are not null
        assertNotNull(shouldBeGalesEvents);

        // Make sure that the list of events is only 2 long (that didnt add Jane's event too)
        assertEquals(2, shouldBeGalesEvents.size());

        // Now make sure that the 2 things in the list of events are the events we expect them to be
        assertEquals(bestEvent, shouldBeGalesEvents.get(0));
        assertEquals(anotherBestEvent, shouldBeGalesEvents.get(1));

        // That should cover all wrong things could run into here...
    }

    @Test
    public void findAllUsersEventsFail() throws DataAccessException {
        // Know will fail if try to find the events for a different user (or it should fail in that case...)
        // So create event's for Gale and then try to find the events for user Jane
        eDao.insert(bestEvent);
        eDao.insert(anotherBestEvent);

        List<Event> findJanesEvent = eDao.findAllUsersEvents("Jane");
        assertNull(findJanesEvent);   // Will be null bc made events for Gale, not Jane, so shouldnt be any events to find!
    }

    @Test
    public void findPersonBirthEventPass() throws DataAccessException {
        // Try find the event before make it to make sure it's actually null how it should be
        Event findGaleBirth = eDao.find(galeBirthEvent.getEventID());

        // Also make a non-birth event to make sure do NOT find it - only want find birth ones here!
        Event findNonBirthEvent = eDao.find(bestEvent.getEventID());

        assertNull(findGaleBirth);
        assertNull(findNonBirthEvent);

        // Now actually create the events
        eDao.insert(galeBirthEvent);
        eDao.insert(bestEvent);

        // Now try find the birth event
        findGaleBirth = eDao.findPersonBirthEvent("Gale123A");

        // Make sure it actually added
        assertNotNull(findGaleBirth);

        // Make sure it equals the one that we think it does (the birth one)
        assertEquals(galeBirthEvent, eDao.findPersonBirthEvent("Gale123A"));

        // Make sure that doesnt equal the non birth Gale event
        assertNotEquals(bestEvent, eDao.findPersonBirthEvent("Gale123A"));

        // Make sure still didnt find the non birth Gale event
        assertNull(findNonBirthEvent);

        // Make sure the event type for the event found is birth
        assertEquals("birth", findGaleBirth.getEventType());
    }

    @Test
    public void findPersonBirthEventFail() throws DataAccessException {
        // Know it would fail by finding birth event for user that has no birth events

        // So try find birth event for Jane (dont have any)
        eDao.insert(forAnotherUser);

        // And find the non birth one and assert null it
        Event findEvent = eDao.findPersonBirthEvent("Jane333");     // best event has an eventID of Biking_123A not 333 so this should make it fail
        assertNull(findEvent);

        // Also try to find birth event for different user when only have birth event for different user made
        eDao.insert(galeBirthEvent);

        Event findBirthEvent = eDao.findPersonBirthEvent("Jane333");
        assertNull(findBirthEvent);
    }

    @Test
    public void findPersonDeathEventPass() throws DataAccessException {
        // Try find the event before make it to make sure it's actually null how it should be
        Event findGaleDeath = eDao.find(galeDeathEvent.getEventID());

        // Also make a non-birth event to make sure do NOT find it - only want find birth ones here!
        Event findNonDeathEvent = eDao.find(bestEvent.getEventID());

        assertNull(findGaleDeath);
        assertNull(findNonDeathEvent);

        // Now actually create the events
        eDao.insert(galeDeathEvent);
        eDao.insert(bestEvent);

        // Now try find the birth event
        findGaleDeath = eDao.findPersonDeathEvent("Gale123A");

        // Make sure it actually added
        assertNotNull(findGaleDeath);

        // Make sure it equals the one that we think it does (the birth one)
        assertEquals(galeDeathEvent, eDao.findPersonDeathEvent("Gale123A"));

        // Make sure that doesnt equal the non birth Gale event
        assertNotEquals(bestEvent, eDao.findPersonDeathEvent("Gale123A"));

        // Make sure still didnt find the non birth Gale event
        assertNull(findNonDeathEvent);

        // Make sure the event type for the event found is birth
        assertEquals("death", findGaleDeath.getEventType());
    }

    @Test
    public void findPersonDeathEventFail() throws DataAccessException {
        // Know it would fail by finding birth event for user that has no birth events

        // So try find birth event for Jane (dont have any)
        eDao.insert(forAnotherUser);

        // And find the non birth one and assert null it
        Event findEvent = eDao.findPersonDeathEvent("Jane333");     // best event has an eventID of Biking_123A not 333 so this should make it fail
        assertNull(findEvent);

        // Also try to find birth event for different user when only have birth event for different user made
        eDao.insert(galeDeathEvent);

        Event findBirthEvent = eDao.findPersonDeathEvent("Jane333");
        assertNull(findBirthEvent);
    }

    // Testing to make sure actually clear entire Event table in DB
    // When call this test the DB has been cleared in BeforeEach set up so have empty Event table when enter this test
    // Which means that when test this, just need to add one event and then make sure actually deletes that one event
    @Test
    public void clearTest() throws DataAccessException {
        eDao.insert(bestEvent);                                 // add an event into Event table
        // grab the event that should have just added into the table out of the table
        Event grabEvent = eDao.find("Biking_123A");     // this is the eventID that should be assoicated with event just made (bc one in set up func have)
        assertNotNull(grabEvent);                               // check that the event was actually added into table (if wasnt added then will be null)
        eDao.clear();                                           // now clear the Event table
        // now grab the event out of table again (should have just cleared table so it should be null now)
        grabEvent = eDao.find("Biking_123A");
        assertNull(grabEvent);
    }

    @Test
    public void clearForUserTest() throws DataAccessException {
        eDao.insert(bestEvent);          // Add 1st event for user Gale
        eDao.insert(anotherBestEvent);   // Add 2nd event for user Gale

        // Also create an event for a different user that isn't Gale so can make sure when delete events for Gale that don't delete them for this other person too!
        eDao.insert(forAnotherUser);

        // Grab the 2 events that should have just added into the table out of the table
        Event grabEvent = eDao.find("Biking_123A");
        Event grabSecondEvent = eDao.find("Biking_123B");
        Event grabOtherPersonsEvent = eDao.find("Something_Cool");

        // Make sure they were actually added into table with an assertNotNull
        assertNotNull(grabEvent);
        assertNotNull(grabSecondEvent);
        assertNotNull(grabOtherPersonsEvent);

        // Now clear the events for Gale
        eDao.clearForUser("Gale");

        // Make sure get assertNull when try to grab those events for Gale now
        grabEvent = eDao.find("Biking_123A");
        grabSecondEvent = eDao.find("Biking_123B");

        assertNull(grabEvent);
        assertNull(grabSecondEvent);

        // Now grab the other persons event and make sure it is NOT null still
        grabOtherPersonsEvent = eDao.find("Something_Cool");
        assertNotNull(grabOtherPersonsEvent);

        // And make sure that the event there is the one that had there always
        assertEquals(forAnotherUser, eDao.find("Something_Cool"));
    }
}