package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.Review;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            statement.close();
        }
        catch (SQLException ex) {
             System.out.println(ex);
        }
    }

    public void createHotelTable(){
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_HOTEL_TABLE);
            statement.close();
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void createHotelFavoritesTable(){


    }

    public void createExpediaLinksTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_EXPEDIA_TABLE);
            statement.close();
            System.out.println("Expedia Links table created");
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public void insertExpedia(String hotelId, String hotelName, String user, String dateVisited){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.INSERT_EXPEDIA_LINK);
                statement.setString(1, hotelId);
                statement.setString(2, hotelName);
                statement.setString(3, user);
                statement.setString(4, dateVisited);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public JsonArray findExpediaLinks(String user){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("finding expedia links for " + user + "...");
            statement = connection.prepareStatement(PreparedStatements.FIND_EXPEDIA_LINKS);

            statement.setString(1,  user);
            ResultSet results = statement.executeQuery();

            JsonArray linksArr = new JsonArray();
            while (results.next()){
                JsonObject temp = new JsonObject();
                String id = results.getString("hotelId");
                String hotelName = results.getString("hotelName");
                String username = results.getString("user");
                String dateVisited = results.getString("dateVisited");

                temp.addProperty("hotelId", id);
                temp.addProperty("hotelName", hotelName);
                temp.addProperty("user", username);
                temp.addProperty("dateVisited", dateVisited);

                linksArr.add(temp);
            }
            statement.close();
            results.close();
            return linksArr;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;

    }

    public void clearExpediaLinks(String user){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                System.out.println("Deleting expedia links for : " + user + " from db...");
                statement = connection.prepareStatement(PreparedStatements.CLEAR_EXPEDIA_LINKS);
                statement.setString(1, user);
                statement.executeUpdate();
                statement.close();
                System.out.println("Deleted expedia links from db.");
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }


    public void createReviewTable(){
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_REVIEW_TABLE);
            statement.close();
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public void insertHotel(Hotel hotel){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.INSERT_HOTEL);
                statement.setString(1, hotel.getId());
                statement.setString(2, hotel.getName());
                statement.setString(3, hotel.getLatitude());
                statement.setString(4, hotel.getLongitude());
                statement.setString(5, hotel.getAddress());
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void insertReviews(Review review){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
//                reviewId, hotelId, ratingOverall, title, reviewText, userNickname, datePosted
                statement = connection.prepareStatement(PreparedStatements.INSERT_REVIEW);
                statement.setString(1, review.getReviewID());
                statement.setString(2, review.getHotelID());
                statement.setString(3, review.getRatingOverall());
                statement.setString(4, review.getTitle());
                statement.setString(5, review.getReviewText());
                statement.setString(6, review.getUserNickname());
                statement.setString(7, review.getDatePosted().toString());
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
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

    public Hotel findHotel(String hotelId){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("finding hotel " + hotelId + " in db...");
            statement = connection.prepareStatement(PreparedStatements.FIND_HOTEL);

            statement.setString(1, hotelId);
            ResultSet results = statement.executeQuery();

            Hotel tempHotel = null;
            while (results.next()){
                String id = results.getString("hotelId");
                String hotelName = results.getString("hotelName");
                String latitude = results.getString("latitude");
                String longitude = results.getString("longitude");
                String address = results.getString("address");

                tempHotel = new Hotel(hotelName, id, latitude, longitude, address);
                break;
            }
            statement.close();
            results.close();
            return tempHotel;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;

    }

    public Review findReview(String reviewId){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.FIND_REVIEW);

            statement.setString(1, reviewId);
            ResultSet results = statement.executeQuery();

            Review tempReview = null;
            while (results.next()){
                String id = results.getString("reviewId");
                String hotelId = results.getString("hotelId");
                String rating = results.getString("ratingOverall");
                String title = results.getString("title");
                String reviewText = results.getString("reviewText");
                String user = results.getString("userNickname");
                String date = results.getString("datePosted");

                tempReview = new Review(hotelId, reviewId, rating, title, reviewText, user, date);
                break;
            }
            statement.close();
            results.close();
            return tempReview;

        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;

    }

    public List<Review> findHotelReviews(String hotelId){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("finding reviews for: " + hotelId + " in db...");
            statement = connection.prepareStatement(PreparedStatements.GET_REVIEW_LIST);

            statement.setString(1, hotelId);
            ResultSet results = statement.executeQuery();


            List<Review> reviews = new ArrayList<>();
            while (results.next()){
                Review tempReview;
                String reviewId = results.getString("reviewId");
                String hId = results.getString("hotelId");
                String rating = results.getString("ratingOverall");
                String title = results.getString("title");
                String reviewText = results.getString("reviewText");
                String user = results.getString("userNickname");
                String date = results.getString("datePosted");

                tempReview = new Review(hId, reviewId, rating, title, reviewText, user, date);
                reviews.add(tempReview);
            }

            statement.close();
            results.close();
            return reviews;

        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Review> findHotelReviewsLimit(String hotelId, int limit, int offset){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("finding reviews for: " + hotelId + " in db...");
            statement = connection.prepareStatement(PreparedStatements.GET_REVIEW_LIST_LIMIT);

            statement.setString(1, hotelId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

            ResultSet results = statement.executeQuery();

            List<Review> reviews = new ArrayList<>();
            while (results.next()){
                Review tempReview;
                String reviewId = results.getString("reviewId");
                String hId = results.getString("hotelId");
                String rating = results.getString("ratingOverall");
                String title = results.getString("title");
                String reviewText = results.getString("reviewText");
                String user = results.getString("userNickname");
                String date = results.getString("datePosted");

                tempReview = new Review(hId, reviewId, rating, title, reviewText, user, date);
                reviews.add(tempReview);
            }
            for (Review r: reviews){
                System.out.println(r.getReviewID() + " " + r.getUserNickname());
            }

            statement.close();
            results.close();
            return reviews;

        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Hotel> hotelKeywordSearch (String keyword){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("finding hotel with " + keyword + " in hotelName db...");
            statement = connection.prepareStatement(PreparedStatements.HOTEL_KEYWORD_SEARCH);

            statement.setString(1, "%"+keyword+"%");
            ResultSet results = statement.executeQuery();

            List<Hotel> hotels = new ArrayList<>();
            while (results.next()){
                Hotel tempHotel;
                String id = results.getString("hotelId");
                String hotelName = results.getString("hotelName");
                String latitude = results.getString("latitude");
                String longitude = results.getString("longitude");
                String address = results.getString("address");

                tempHotel = new Hotel(hotelName, id, latitude, longitude, address);
                hotels.add(tempHotel);
            }
            statement.close();
            results.close();
            return hotels;

        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public void deleteReview(String reviewId){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                System.out.println("Deleting review: " + reviewId + " from db...");
                statement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW);
                statement.setString(1, reviewId);
                statement.executeUpdate();
                statement.close();
                System.out.println("Deleted review from db.");
            }
            catch(SQLException e) {
                System.out.println(e);
                String[] eSplit = e.toString().split(": ");
            }
        }
        catch (SQLException ex) {
            String[] eSplit = ex.toString().split(": ");
        }
    }

    public void editReview(Review review, String editedTitle, String editedText, String editedRating){
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                System.out.println("Editing review: " + review.getReviewID() + " in db...");
                statement = connection.prepareStatement(PreparedStatements.EDIT_REVIEW);
                if (editedTitle != null){
                    statement.setString(1, editedTitle);
                }
                else {
                    statement.setString(1, review.getTitle());
                }
                if (editedText != null){
                    statement.setString(2, editedText);
                }
                else {
                    statement.setString(2, review.getReviewText());
                }
                if (editedRating != null){
                    statement.setString(3, editedRating);
                }
                else{
                    statement.setString(3, review.getRatingOverall());
                }

                statement.setString(4, review.getReviewID());
                statement.executeUpdate();
                statement.close();
                System.out.println("Edited review in db.");
            }
            catch(SQLException e) {
                System.out.println(e);
                String[] eSplit = e.toString().split(": ");
            }
        }
        catch (SQLException ex) {
            String[] eSplit = ex.toString().split(": ");
        }
    }

    public String getAvgRating(String hotelId){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.AVG_RATING);
                statement.setString(1, hotelId);
                ResultSet results = statement.executeQuery();

                String rating = "";

                while (results.next()){
                    rating = results.getString("Average Rating");
                }

                statement.close();
                return rating;
            }
            catch(SQLException e) {
                System.out.println(e);
                String[] eSplit = e.toString().split(": ");
            }
        }
        catch (SQLException ex) {
            String[] eSplit = ex.toString().split(": ");
        }

        return null;
    }

    public void createLastLoginTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_LAST_LOGIN_TABLE);
            statement.close();
            System.out.println("Last Login table created");
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public void insertLogin(String user, String date) {
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.INSERT_LOGIN);
                statement.setString(1, user);
                statement.setString(2, date);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public void updateLogin(String user, String date) {
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.UPDATE_LOGIN);
                statement.setString(1, date);
                statement.setString(2, user);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public String findLogin(String user){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.FIND_LOGIN);
                statement.setString(1, user);
                ResultSet results = statement.executeQuery();

                String login = "";

                while (results.next()){
                    login = results.getString("lastLogin");
                }

                statement.close();
                return login;
            }
            catch(SQLException e) {
                System.out.println(e);
                String[] eSplit = e.toString().split(": ");
            }
        }
        catch (SQLException ex) {
            String[] eSplit = ex.toString().split(": ");
        }

        return null;
    }

    public int reviewCount(String hotelId){
        PreparedStatement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.REVIEW_COUNT);
                statement.setString(1, hotelId);
                ResultSet results = statement.executeQuery();

                int count = 0;

                while (results.next()){
                    count = results.getInt("COUNT(*)");
                }

                statement.close();
                return count;
            }
            catch(SQLException e) {
                System.out.println(e);
                String[] eSplit = e.toString().split(": ");
            }
        }
        catch (SQLException ex) {
            String[] eSplit = ex.toString().split(": ");
        }

        return 0;

    }


//    public static void main(String[] args) {
//        DatabaseHandler dhandler = DatabaseHandler.getInstance();
//        dhandler.createHotelTable();
//        dhandler.createReviewTable();
//        dbHandler.createExpediaLinksTable();
//        System.out.println("created a hotel table ");
//        dhandler.registerUser("luke", "lukeS1k23w");
//        System.out.println("Registered luke.");

//        dbHandler.insertExpedia("12539", "Expedia/Hilton", "Adi", "2022-12-06");
//        dbHandler.insertExpedia("12121", "Expedia/Hilton", "Adi", "2022-11-05");
//        dbHandler.insertExpedia("12539", "Expedia/Hilton", "Test", "2022-12-01");

//        JsonArray linksArr = dbHandler.findExpediaLinks("Adi");
//        System.out.println(linksArr);
//        for (JsonElement je: linksArr){
//            String id = ((JsonObject)je).get("hotelId").getAsString();
//            System.out.println(id);
//        }

//        dbHandler.clearExpediaLinks("Adi");



//        Hotel h = dhandler.findHotel("12539");
//        System.out.println(h);
//
//        Review r = dbHandler.findReview("57b5d78e65534f0b7741a9c6");
//        System.out.println(r);

//        List<Review> reviews = dbHandler.findHotelReviewsLimit("12539", 5, 0);
//        System.out.println("Offset: " + 0);
//        for (Review r: reviews){
//            System.out.println(r.getReviewID() + " " + r.getUserNickname());
//        }
//
//        System.out.println();
//        List<Review> reviews2 = dbHandler.findHotelReviewsLimit("12539", 5, 5);
//        System.out.println("Offset: " + 5);
//        for (Review r: reviews2){
//            System.out.println(r.getReviewID() + " " + r.getUserNickname());
//        }
//
////        List<Hotel> hotels = dhandler.hotelKeywordSearch("Hilton");
////        System.out.println(hotels);
////        System.out.println(hotels.size());
//
//        Review addR = new Review("12539", "111222", "4.0", "test",
//                "testText", "Adi", "2021-12-11");
//        dbHandler.insertReviews(addR);
//
//        Review r = dbHandler.findReview("111222");
//        System.out.println(r);
//
//        dbHandler.editReview(r, "editedTitle", "editedText", "1.5");
//        r = dbHandler.findReview("111222");
//        System.out.println(r);
//
//
//        dbHandler.deleteReview("111222");
////
//        reviews = dbHandler.findHotelReviews("12539");
//        System.out.println(reviews.size());
//        double rating = dhandler.getAvgRating("12539");
//        System.out.println(rating);
//        dbHandler.createLastLoginTable();

//        LocalDateTime now = LocalDateTime.now();
//        String dtNow = dtf.format(now);

//        dbHandler.insertLogin("Adi", dtNow);
//        dbHandler.updateLogin("Adi", dtNow);
//        String lastLogin = dbHandler.findLogin("A");
//        System.out.println("Last Login: " + lastLogin);

//        System.out.println(dbHandler.reviewCount("12539"));


//    }
}

