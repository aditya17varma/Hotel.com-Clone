package jettyServer;

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
                    "ratingOverall DOUBLE NOT NULL, " +
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

    public static final String FIND_HOTEL =
            "SELECT * FROM hotels WHERE hotelId = ?";

    public static final String FIND_REVIEW =
            "SELECT * FROM reviews WHERE reviewId = ?";

    public static final String GET_REVIEW_LIST =
            "SELECT * FROM reviews WHERE hotelId = ? ORDER BY datePosted DESC";

    public static final String HOTEL_KEYWORD_SEARCH =
            "SELECT * FROM hotels WHERE hotelName like ?";

    public static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE reviewId = ?";

    public static final String EDIT_REVIEW =
            "UPDATE reviews SET title = ?," +
                    "reviewText = ?," +
                    "ratingOverall = ? WHERE reviewId = ?";

    public static final String AVG_RATING =
            "SELECT AVG(ratingOverall) 'Average Rating' FROM reviews WHERE hotelId = ?";

    /**
     * For creating Expedia table
     */
    public static final String CREATE_EXPEDIA_TABLE =
            "CREATE TABLE expedia (" +
                    "hotelId VARCHAR(64), " +
                    "expediaLink TEXT NOT NULL, " +
                    "user VARCHAR(64) NOT NULL," +
                    "PRIMARY KEY(hotelId, user));";

    public static final String INSERT_EXPEDIA_LINK =
            "INSERT IGNORE INTO expedia (hotelId, expediaLink, user) " +
                    "VALUES (?, ?, ?);";

    public static final String FIND_EXPEDIA_LINKS =
            "SELECT * FROM expedia WHERE user = ?";

    public static final String CLEAR_EXPEDIA_LINKS =
            "DELETE FROM expedia WHERE hotelId = ? AND user = ?;";


}
