package jettyServer;

import java.time.LocalDate;

public class PreparedStatements {
    /** Prepared Statements  */
    /** For creating the users table */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    /** Used to insert a new user into the database. */
    public static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";

    /** Used to retrieve the salt associated with a specific user. */
    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?";

    /** Used to authenticate a user. */
    public static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ?";

    /**
     * For creating Hotels table
     */
    public static final String CREATE_HOTEL_TABLE =
            "CREATE TABLE hotels (" +
                    "hotelId VARCHAR(64) PRIMARY KEY, " +
                    "hotelName VARCHAR(64) NOT NULL, " +
                    "latitude VARCHAR(64) NOT NULL, " +
                    "longitude VARCHAR(64) NOT NULL, " +
                    "address VARCHAR(64) NOT NULL);";

    /**
     * For inserting hotel data into hotels table
     */
    public static final String INSERT_HOTEL =
            "INSERT IGNORE INTO hotels (hotelId, hotelName, latitude, longitude, address) " +
                    "VALUES (?, ?, ?, ?, ?);";

    /**
     * For creating reviews table
     */
    public static final String CREATE_REVIEW_TABLE =
            "CREATE TABLE reviews (" +
                    "reviewId VARCHAR(64) PRIMARY KEY, " +
                    "hotelId VARCHAR(64) NOT NULL, " +
                    "ratingOverall VARCHAR(64) NOT NULL, " +
                    "title VARCHAR(64) NOT NULL, " +
                    "reviewText TEXT NOT NULL, " +
                    "userNickname VARCHAR(64) NOT NULL, " +
                    "datePosted DATE NOT NULL);";

    /**
     * For inserting review data into reviews table
     */
    public static final String INSERT_REVIEW =
            "INSERT IGNORE INTO reviews (reviewId, hotelId, ratingOverall, title, reviewText, userNickname, datePosted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

}
