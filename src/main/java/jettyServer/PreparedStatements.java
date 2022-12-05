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
     * id, name, latitude, longitude, address
     */
    public static final String CREATE_HOTEL_TABLE =
            "CREATE TABLE hotels (" +
                    "hotelId VARCHAR(64) PRIMARY KEY, " +
                    "hotelName VARCHAR(64) NOT NULL, " +
                    "latitude VARCHAR(64) NOT NULL, " +
                    "longitude VARCHAR(64) NOT NULL, " +
                    "address VARCHAR(64) NOT NULL);";

    public static final String INSERT_HOTEL =
            "INSERT IGNORE INTO hotels (hotelId, hotelName, latitude, longitude, address) " +
                    "VALUES (?, ?, ?, ?, ?);";


}
