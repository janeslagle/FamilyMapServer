package DAOTests;

import Model.Event;
import Model.User;
import DAO.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class userDAOTest {
    private Database db;
    private User bestUser;
    private User anotherUser;
    private User thirdUser;
    private userDAO uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new user with random data
        // user has username, password, email, first name, last name, gender and person ID associated with them
        bestUser = new User("testUsername", "testPassword", "testEmail", "Jane", "Slagle", "f", "333");

        anotherUser = new User("sally", "sallyRocks", "sallyEmail", "Sally", "Brown", "f", "45435sdf");

        thirdUser = new User("jane", "janeRocks!", "janeEmail", "Camille", "Casey", "f", "fshfksdf");

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the UserDAO, so it can access the database.
        uDao = new userDAO(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        uDao.clear();
    }

    @AfterEach
    public void tearDown() {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }

    @Test
    public void createNewUserPass() throws DataAccessException {
        // Start by creating a new user in the DB.
        uDao.createNewUser(bestUser);
        // Let's use a find method to get the user that we just put in back out.
        User compareTest = uDao.getUserInfo(bestUser.getUsername());
        // First lets see if our find method found anything at all. If it did then we know that we got
        // something back from our database.
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is the same as what we got out. If this
        // passes then we know that our insert did put something in, and that it didn't change the
        // data in any way.
        // This assertion works by calling the equals method in the User class.
        assertEquals(bestUser, compareTest);
    }

    @Test
    public void createNewUserFail() throws DataAccessException {
        // Let's do this test again, but this time lets try to make it fail.
        // If we call the method the first time the event will be inserted successfully.
        uDao.createNewUser(bestUser);

        // However, our sql table is set up so that the column "username" must be unique, so trying to insert
        // the same user again will cause the insert method to throw an exception, and we can verify this
        // behavior by using the assertThrows assertion as shown below.

        // Note: This call uses a lambda function. A lambda function runs the code that comes after
        // the "()->", and the assertThrows assertion expects the code that ran to throw an
        // instance of the class in the first parameter, which in this case is a DataAccessException.
        assertThrows(DataAccessException.class, () -> uDao.createNewUser(bestUser));
    }

    @Test
    public void getUserInfoPass() throws DataAccessException {
        User findUser = uDao.getUserInfo(bestUser.getUsername());
        assertNull(findUser);

        uDao.createNewUser(bestUser);
        findUser = uDao.getUserInfo(bestUser.getUsername());
        assertNotNull(findUser);
    }

    @Test
    public void getUserInfoFail() throws DataAccessException {
        uDao.createNewUser(bestUser);
        User findUser = uDao.getUserInfo("jane");  //bestUser was created with username testUsername so this will be null
        assertNull(findUser);
    }

    @Test
    public void validateLoginPass() throws DataAccessException {
        // First create the user that want validate / check the login for
        uDao.createNewUser(bestUser);
        uDao.createNewUser(anotherUser);
        uDao.createNewUser(thirdUser);

        // Now validate them
        // Know will be true
        boolean shouldBeSuccessful = uDao.validateLogin("testUsername", "testPassword");
        assertTrue(shouldBeSuccessful);

        // Now do case where will know it fails bc wrong password
        boolean shouldFail = uDao.validateLogin("testUsername", "janeRocks!");
        assertFalse(shouldFail);

        // Now do case where will know it fails bc wrong username, but right password
        boolean failsAgain = uDao.validateLogin("jane", "testPassword");
        assertFalse(failsAgain);

        boolean shouldWork = uDao.validateLogin("sally", "sallyRocks");
        assertTrue(shouldWork);

        boolean willWork = uDao.validateLogin("jane", "janeRocks!");
        assertTrue(willWork);
    }

    @Test
    public void validateLoginFail() throws DataAccessException {
     // Know will fail when put in wrong password and wrong username
     uDao.createNewUser(bestUser);
     uDao.createNewUser(anotherUser);

     boolean willBeWrong = uDao.validateLogin("wrongUsername", "wrongPassword");
     assertFalse(willBeWrong);

     boolean userNotCreated = uDao.validateLogin("jane", "janeRocks!");
     assertFalse(userNotCreated);

     boolean wrongCombo = uDao.validateLogin("sally", "testPassword");
     assertFalse(wrongCombo);
    }

    // Testing to make sure actually clear entire User table
    // Same exact idea as clearTest method have in EventDAOTest class, but for UserDAOTest now
    @Test
    public void clearTest() throws DataAccessException {
        uDao.createNewUser(bestUser);                               // add a user into User table
        // grab the user that should have just added into the table out of the table
        User grabUser = uDao.getUserInfo("testUsername");  // if actually created new user, won't be null
        assertNotNull(grabUser);                                    // check that the user was actually added into table
        uDao.clear();                                               // now clear the User table
        // now grab the user out of table again (should have just cleared table so it should be null now)
        grabUser = uDao.getUserInfo("testUsername");
        assertNull(grabUser);
    }
}