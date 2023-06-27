package DAOTests;

import Model.Person;
import DAO.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class personDAOTest {
    private Database db;
    private Person bestPerson;
    private Person anotherBestPerson;
    private Person thirdOne;
    private personDAO pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new person with random data
        // person has personID, associatedUsername, firstName, lastName, gender, fatherID, motherID and spouseID associated with them
        bestPerson = new Person("testPersonID", "testAssociatedUsername", "Jane", "Slagle",
                "f", "Nick", "Tracy", "testSpouseID");

        anotherBestPerson = new Person("person2", "Jane", "Kate", "Middleton",
                "f", "Matt", "Carol", "William");

        thirdOne = new Person("person3", "testAssociatedUsername", "Jane", "Slagle",
                "f", "Ed", "Pam", "Sammy");

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the UserDAO, so it can access the database.
        pDao = new personDAO(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        pDao.clear();
    }

    @AfterEach
    public void tearDown() {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }

    @Test
    public void createNewPersonPass() throws DataAccessException {
        // Start by creating a new person in the DB.
        pDao.createNewPerson(bestPerson);
        // Let's use a find method to get the person that we just put in back out.
        Person compareTest = pDao.getPersonByID("testPersonID");
        // First lets see if our find method found anything at all. If it did then we know that we got
        // something back from our database.
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is the same as what we got out. If this
        // passes then we know that our insert did put something in, and that it didn't change the
        // data in any way.
        // This assertion works by calling the equals method in the User class.
        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void createNewPersonFail() throws DataAccessException {
        // Let's do this test again, but this time lets try to make it fail.
        // If we call the method the first time the event will be inserted successfully.
        pDao.createNewPerson(bestPerson);

        // However, our sql table is set up so that the column "username" must be unique, so trying to insert
        // the same user again will cause the insert method to throw an exception, and we can verify this
        // behavior by using the assertThrows assertion as shown below.

        // Note: This call uses a lambda function. A lambda function runs the code that comes after
        // the "()->", and the assertThrows assertion expects the code that ran to throw an
        // instance of the class in the first parameter, which in this case is a DataAccessException.
        assertThrows(DataAccessException.class, () -> pDao.createNewPerson(bestPerson));
    }

    @Test
    public void getUserPeopleInfoPass() throws DataAccessException {
        // First try to find all of the people before create them to make sure it gives you null!
        Person findBestPerson = pDao.getPersonByID(bestPerson.getPersonID());
        Person findThirdOne = pDao.getPersonByID(thirdOne.getPersonID());
        Person findAnotherBestPerson = pDao.getPersonByID(anotherBestPerson.getPersonID());
        assertNull(findBestPerson);
        assertNull(findThirdOne);
        assertNull(findAnotherBestPerson);

        pDao.createNewPerson(bestPerson);
        pDao.createNewPerson(thirdOne);
        pDao.createNewPerson(anotherBestPerson);

        // Now find all events for user Gale and make sure it isn't null! (bc they should be added now!)
        // Need be stored in a list of events so make that
        List<Person> shouldBeFirstUsersPeople = pDao.getUserPeopleInfo("testAssociatedUsername");

        // Make sure that shouldBeGalesEvents are not null
        assertNotNull(shouldBeFirstUsersPeople);

        // Make sure that the list of events is only 2 long (that didnt add Jane's event too)
        assertEquals(2, shouldBeFirstUsersPeople.size());

        // Now make sure that the 2 things in the list of events are the events we expect them to be
        assertEquals(bestPerson, shouldBeFirstUsersPeople.get(0));
        assertEquals(thirdOne, shouldBeFirstUsersPeople.get(1));

        // That should cover all wrong things could run into here...
    }

    @Test
    public void getUserPeopleInfoFail() throws DataAccessException {
        // Know will fail if try to find the events for a different user (or it should fail in that case...)
        // So create event's for Gale and then try to find the events for user Jane
        pDao.createNewPerson(bestPerson);
        pDao.createNewPerson(thirdOne);

        List<Person> findJanesPeople = pDao.getUserPeopleInfo("Jane");
        assertNull(findJanesPeople);   // Will be null bc made events for Gale, not Jane, so shouldnt be any events to find!
    }

    @Test
    public void getPersonByIDPass() throws DataAccessException {
        Person findPerson = pDao.getPersonByID(bestPerson.getPersonID());
        assertNull(findPerson);

        pDao.createNewPerson(bestPerson);
        findPerson = pDao.getPersonByID(bestPerson.getPersonID());
        assertNotNull(findPerson);
    }

    @Test
    public void getPersonByIDFail() throws DataAccessException {
        pDao.createNewPerson(bestPerson);
        Person findPerson = pDao.getPersonByID("333");  //bestPerson was created with personID testPersonID so this will be null
        assertNull(findPerson);
    }

    @Test
    public void getPersonByFatherIDPass() throws DataAccessException {
        Person findPerson = pDao.getPersonByFatherID(bestPerson.getFatherID());
        assertNull(findPerson);

        pDao.createNewPerson(bestPerson);
        findPerson = pDao.getPersonByFatherID(bestPerson.getFatherID());
        assertNotNull(findPerson);
    }

    @Test
    public void getPersonByFatherIDFail() throws DataAccessException {
        pDao.createNewPerson(bestPerson);
        Person findPerson = pDao.getPersonByFatherID("Mark");  //bestPerson was created with fatherID Nick so this will be null
        assertNull(findPerson);
    }

    @Test
    public void getPersonByMotherIDPass() throws DataAccessException {
        Person findPerson = pDao.getPersonByMotherID(bestPerson.getMotherID());
        assertNull(findPerson);

        pDao.createNewPerson(bestPerson);
        findPerson = pDao.getPersonByMotherID(bestPerson.getMotherID());
        assertNotNull(findPerson);
    }

    @Test
    public void getPersonByMotherIDFail() throws DataAccessException {
        pDao.createNewPerson(bestPerson);
        Person findPerson = pDao.getPersonByMotherID("Mary");  //bestPerson was created with fatherID Tracy so this will be null
        assertNull(findPerson);
    }

    @Test
    public void getPersonBySpouseIDPass() throws DataAccessException {
        Person findPerson = pDao.getPersonBySpouseID(bestPerson.getSpouseID());
        assertNull(findPerson);

        pDao.createNewPerson(bestPerson);
        findPerson = pDao.getPersonBySpouseID(bestPerson.getSpouseID());
        assertNotNull(findPerson);
    }

    @Test
    public void getPersonBySpouseIDFail() throws DataAccessException {
        pDao.createNewPerson(bestPerson);
        Person findPerson = pDao.getPersonBySpouseID("Sammy");  //bestPerson was created with spouseID testSpouseID so this will be null
        assertNull(findPerson);
    }

    // Testing to make sure actually clear entire Person table
    // Same exact idea as clearTest method have in EventDAOTest class, but for UserDAOTest now
    @Test
    public void clearTest() throws DataAccessException {
        pDao.createNewPerson(bestPerson);                                 // add a person into Person table
        // grab the person that should have just added into the table out of the table
        Person grabPerson = pDao.getPersonByID("testPersonID");   // if actually created new person, won't be null
        assertNotNull(grabPerson);                                        // check that the person was actually added into table
        pDao.clear();                                                     // now clear the Person table
        // now grab the person out of table again (should have just cleared table so it should be null now)
        grabPerson = pDao.getPersonByID("testPersonID");
        assertNull(grabPerson);
    }

    @Test
    public void clearAllUserPeopleTest() throws DataAccessException {
        pDao.createNewPerson(bestPerson);            // User 1
        pDao.createNewPerson(thirdOne);              // User 1 as well
        pDao.createNewPerson(anotherBestPerson);     // User 2


        // Grab the people that should have just added into the table out of the table to make sure they were added
        Person grabBestPerson = pDao.getPersonByID("testPersonID");
        Person grabThirdOne = pDao.getPersonByID("person3");
        Person grabOtherPerson = pDao.getPersonByID("person2");

        // Make sure they were actually added into table with an assertNotNull
        assertNotNull(grabBestPerson);
        assertNotNull(grabThirdOne);
        assertNotNull(grabOtherPerson);

        // Now clear the events for Gale
        pDao.clearAllUserPeople("testAssociatedUsername");

        // Make sure get assertNull when try to grab those events for Gale now
        grabBestPerson = pDao.getPersonByID("testPersonID");
        grabThirdOne = pDao.getPersonByID("person3");

        assertNull(grabBestPerson);
        assertNull(grabThirdOne);

        // Now grab the other persons event and make sure it is NOT null still
        grabOtherPerson = pDao.getPersonByID("person2");
        assertNotNull(grabOtherPerson);

        // And make sure that the event there is the one that had there always
        assertEquals(anotherBestPerson, pDao.getPersonByID("person2"));
    }
}
