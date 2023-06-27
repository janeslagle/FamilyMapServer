package DAO;

import Model.User;

import java.sql.*;

/**
 * This class will access the database to do everything related to users that want to do
 * With users in the DB, will want to create new users, get all needed info. out of the users, update users and delete users
 */
public class userDAO {
    /**
     * This variable will allow the DAO class to access the database (accesses the DB through this connection)
     */
    private final Connection conn;

    /**
     * This method actually connects the connection variable to the DB
     * @param conn The connection variable to the DB (accesses the DB through this variable)
     */
    public userDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Creates a new user in the DB for when someone new registers on the app (follows same structure as insert func in EventDAO class)
     * @param user The user that are creating a new user obj for in the DB (will give us all needed info abt the new user)
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void createNewUser(User user) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO User (username, password, email, firstName, lastName, " +
                "gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a user into the database");
        }
    }

    /**
     * Query the DB for the user that is linked to the inputted username and get all of the info about that user out of it!
     * Will follow same structure as find func from EventDAO class. This is the find method for userDAO class
     * @param username The user and all of its extracted info!
     * @return The user that are searching for
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public User getUserInfo(String username) throws DataAccessException {
        User user;
        ResultSet rs;
        String sql = "SELECT * FROM User WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("personID"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a user in the database");
        }
    }

    /**
     * Will validate if user login failed or passed (if the user was successful at logging in or not)
     * @param username The username of the account logging in that want validate
     * @param password The password (seemingly associated with the inputted username) of the account logging in that want validate
     * @return True if the username and password matched (if are associated w/ an existing account), False otherwise
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public boolean validateLogin(String username, String password) throws DataAccessException {
        User user;
        ResultSet rs;
        String sql = "SELECT * FROM User WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("personID"));
                // This means that the username and password match so we can log the user in so return true!
                if (user.getPassword().equals(password)) {
                    return true;
                }
                // If not, then can't actually log them in bc they're not a valid combo
                else {
                    return false;
                }
            } else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while trying to validate login for the user in the database");
        }
    }

    /**
     * Clears the entire User table, so deletes ALL of the users stored in the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM User";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the User table");
        }
    }
}
