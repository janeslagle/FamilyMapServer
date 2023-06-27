package DAO;

/**
 * Throws an exception when unable to access the data
 */
public class DataAccessException extends Exception {
    DataAccessException(String message) {
        super(message);
    }
}
