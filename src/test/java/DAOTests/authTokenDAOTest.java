package DAOTests;

import Model.Authtoken;
import DAO.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class authTokenDAOTest {
    private Database db;
    private Authtoken bestToken;
    private authTokenDAO aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new person with random data
        // authtoken has authtoken and username parameters
        bestToken = new Authtoken("testAuthtoken", "testUsername");

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the authTokenDAO, so it can access the database.
        aDao = new authTokenDAO(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        aDao.clear();
    }

    @AfterEach
    public void tearDown() {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }

    @Test
    public void createNewTokenPass() throws DataAccessException {
        // Start by creating a new token in the DB.
        aDao.createNewToken(bestToken);
        // Let's use a find method to get the token that we just put in back out.
        Authtoken compareTest = aDao.getAuthToken(bestToken.getAuthToken());
        // First lets see if our find method found anything at all. If it did then we know that we got
        // something back from our database.
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is the same as what we got out. If this
        // passes then we know that our insert did put something in, and that it didn't change the
        // data in any way.
        // This assertion works by calling the equals method in the User class.
        assertEquals(bestToken, compareTest);
    }

    @Test
    public void createNewTokenFail() throws DataAccessException {
        // Let's do this test again, but this time lets try to make it fail.
        // If we call the method the first time the event will be inserted successfully.
        aDao.createNewToken(bestToken);

        // However, our sql table is set up so that the column "username" must be unique, so trying to insert
        // the same user again will cause the insert method to throw an exception, and we can verify this
        // behavior by using the assertThrows assertion as shown below.

        // Note: This call uses a lambda function. A lambda function runs the code that comes after
        // the "()->", and the assertThrows assertion expects the code that ran to throw an
        // instance of the class in the first parameter, which in this case is a DataAccessException.
        assertThrows(DataAccessException.class, () -> aDao.createNewToken(bestToken));
    }

    @Test
    public void getAuthTokenPass() throws DataAccessException {
        Authtoken findToken = aDao.getAuthToken(bestToken.getAuthToken());
        assertNull(findToken);

        aDao.createNewToken(bestToken);
        findToken = aDao.getAuthToken(bestToken.getAuthToken());
        assertNotNull(findToken);
    }

    @Test
    public void getAuthTokenFail() throws DataAccessException {
        aDao.createNewToken(bestToken);
        Authtoken findToken = aDao.getAuthToken("333");  //bestToken was created with authtoken testAuthtoken so this will be null
        assertNull(findToken);
    }

    @Test
    public void clearTest() throws DataAccessException {
        aDao.createNewToken(bestToken);
        Authtoken grabToken = aDao.getAuthToken("testAuthtoken");
        assertNotNull(grabToken);
        aDao.clear();
        grabToken = aDao.getAuthToken("testAuthtoken");
        assertNull(grabToken);
    }
}