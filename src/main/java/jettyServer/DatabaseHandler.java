package jettyServer;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Modified from the example of Prof. Engle
 */
public class DatabaseHandler {

    private static DatabaseHandler dbHandler = new DatabaseHandler("database.properties"); // singleton pattern
    private Properties config; // a "map" of properties
    private String uri = null; // uri to connect to mysql using jdbc
    private Random random = new Random(); // used in password  generation

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private DatabaseHandler(String propertiesFile){
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        //System.out.println("uri = " + uri);
    }

    /**
     * Returns the instance of the database handler.
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return dbHandler;
    }


    // Load info from config file database.properties
    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    public void createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
        }
        catch (SQLException ex) {
             System.out.println(ex);
        }
    }


    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     */
    public String registerUser(String newuser, String newpass) {
        String result = "";

        if (validUser(newuser) && validPassword(newpass)){
            // Generate salt
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);

            String usersalt = encodeHex(saltBytes, 32); // salt
            String passhash = getHash(newpass, usersalt); // hashed password
            System.out.println(usersalt);

            PreparedStatement statement;
            try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
                System.out.println("dbConnection successful");
                try {
                    statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                    statement.setString(1, newuser);
                    statement.setString(2, passhash);
                    statement.setString(3, usersalt);
                    statement.executeUpdate();
                    statement.close();
                    result = "success";
                }
                catch(SQLException e) {
                    System.out.println(e);
                    String[] eSplit = e.toString().split(": ");
                    result = eSplit[1];
                }
            }
            catch (SQLException ex) {
                String[] eSplit = ex.toString().split(": ");
                result = eSplit[1];
            }
        }
        else {
            result = "Invalid username or password.\rUsername must include one Upper, and one Lower character.\r" +
                    "Password must be 8-16 characters, include one Upper, one Lower, one Number and one $ % @ # ! character";
            System.out.println("Could not register");
        }

        return result;
    }

    /**
     * validUser
     * Checks if username has at least one uppercase, and one lower case character
     * @param username username
     * @return true if valid
     */
    public boolean validUser(String username){
        Pattern p = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])");
        Matcher m = p.matcher(username);
        return m.find();
    }

    /**
     * validPassword
     * Checks if password has at least one uppercase, one lowercase and one special character, and is between 8-16 characters long
     * @param password password
     * @return true if valid
     */
    public boolean validPassword(String password){
        Pattern p = Pattern.compile("(?=.{8,16})(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[$%@#!])");
        Matcher m = p.matcher(password);
        return m.find();
    }

    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            //System.out.println("dbConnection successful");
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            System.out.println("SQL Auth Flag: " + flag);
            return flag;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }
}

