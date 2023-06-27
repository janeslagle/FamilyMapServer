package DAO;

import Model.Authtoken;

import java.sql.*;

/**
 * This class will access the DB to do everything related to authtokens that we will want to be able to do within the app
 * Will want to be able to create, find, and delete authorization tokens in the DB
 */
public class authTokenDAO {
    /**
     * This variable will allow the DAO class to access the database (accesses the DB through this connection)
     */
    private final Connection conn;

    /**
     * This method actually connects the connection variable to the DB
     * @param conn The connection variable to the DB (accesses the DB through this variable)
     */
    public authTokenDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Creates a new auth token in the DB (this is the insert function)
     * @param authtoken The auth token that are adding to the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void createNewToken(Authtoken authtoken) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Authtoken (authtoken, username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, authtoken.getAuthToken());
            stmt.setString(2, authtoken.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an authtoken into the database");
        }
    }

    /**
     * Query the DB for authtoken associated with given authtoken ID (each username is paired with authtokens for each login session)
     * This is the find function
     * @param authtoken The authtoken that want find in the DB
     * @return The authtoken associated with the given authtokenID string, otherwise returns null
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public Authtoken getAuthToken(String authtoken) throws DataAccessException {
        Authtoken token;
        ResultSet rs;
        String sql = "SELECT * FROM Authtoken WHERE authtoken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtoken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token = new Authtoken(rs.getString("authtoken"), rs.getString("username"));
                return token;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a user in the database");
        }
    }

    /**
     * Clears the entire Authtoken table, so deletes ALL of the auth tokens stored in the DB
     * @throws DataAccessException Throws an exception if you have an error when try to access the DB
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Authtoken";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table");
        }
    }
}
