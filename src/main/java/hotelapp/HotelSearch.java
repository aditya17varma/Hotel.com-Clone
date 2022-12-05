package hotelapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HotelSearch {
//    private static Logger logger = (Logger) LogManager.getLogger();
    private ThreadSafeHotelData threadSafeHotelData;

    public HotelSearch(){
        this.threadSafeHotelData = new ThreadSafeHotelData();
    }

    /**
     * loadHotels
     * Parses the hotelFile and populates the hotelsMap
     * @param hotelFile file containing the hotels
     */
    public void loadHotels(String hotelFile){
        this.threadSafeHotelData.loadHotels(hotelFile);
    }

    /**
     * loadReviews
     * Parses the reviewDirectory and populates the reviewMap
     * @param reviewDirectory directory containing reviews
     * @param numThreads number of threads to create in multithreaded reviewParser
     */
    public void loadReviews(String reviewDirectory, int numThreads){
        this.threadSafeHotelData.loadReviews(reviewDirectory, numThreads);
    }


    /**
     * createInvertedIndex
     * Creates the inverted index mapping words to Reviews
     */
    public void createInvertedIndex(){
        this.threadSafeHotelData.loadReviewWordCounts();
        this.threadSafeHotelData.createInvertedIndex();
    }

    /**
     * createHotelKeywordMap
     * Maps words in Hotel titles to a Set of Hotel objects
     */
    public void createHotelKeywordMap(){
        this.threadSafeHotelData.createHotelKeywordMap();
    }

    public Set<Hotel> findHotelByKeyword(String word){
        return this.threadSafeHotelData.findHotelByKeyword(word);
    }


    /**
     * findHotel
     * @param id HotelID to search
     * @return Hotel object with matching HotelID if found, null if not found
     */
    public Hotel findHotel(String id){
        return this.threadSafeHotelData.findHotel(id);
    }

    /**
     * findReviews
     * @param hotelID HotelID to search
     * @return ArrayList of HotelReview objects with matching HotelID, empty ArrayList if none found
     */
    public List<Review> findReviews(String hotelID){
        List<Review> foundReviews;

        foundReviews = this.threadSafeHotelData.findReviewList(hotelID);

        return foundReviews;
    }

    /**
     * find Review
     * Finds a particular Review that corrsponds to the given hotelId and reviewId
     * @param hotelId HotelId
     * @param reviewId ReviewId
     * @return Review
     */
    public Review findReview(String hotelId, String reviewId){
        return this.threadSafeHotelData.findReview(hotelId, reviewId);
    }

    /**
     * deleteReview
     * Deletes a review from the HotelData maps
     * @param hotelId hotelId
     * @param reviewId reviewId
     */
    public void deleteReview(String hotelId, String reviewId){
        this.threadSafeHotelData.deleteReview(hotelId, reviewId);
    }

    /**
     * getHotelRating
     * @param hotelId hotelId
     * @return hotelRating
     */
    public double getHotelRating(String hotelId){
        return this.threadSafeHotelData.getHotelRating(hotelId);
    }

    /**
     * modifyHotelRating
     * Modifies the average rating for the Hotel
     * @param hotelId hotelId
     * @param rating reviewRating
     * @param add true if adding, false if deleting
     */
    public void modifyHotelRating(String hotelId, int rating, boolean add){
        this.threadSafeHotelData.modifyHotelRating(hotelId, rating, add);
    }

    /**
     * findWord
     * Searches the invertedIndex for the word
     * @param word word
     * @return ArrayList of reviewIDs that contain the word in their review text
     */
    public ArrayList<ReviewWordCount> findWord(String word){
        ArrayList<ReviewWordCount> reviewsArr = this.threadSafeHotelData.getReviewWordCountArray(word);

        return reviewsArr;
    }

    /**
     * checkReviewIds
     * Checks if a Review is present in the HotelData maps
     * @param id reviewId
     * @return true if present
     */
    public boolean checkReviewIds(String id){
        return this.threadSafeHotelData.checkReviewIds(id);
    }

    /**
     * addReview
     * Adds a review to the HotelData maps
     * @param r Review
     */
    public void addReview(Review r){
        this.threadSafeHotelData.addReviewToReviewMap(r);
    }

    /**
     * Takes a host and a string containing path/resource/query and creates a
     * string of the HTTP GET request
     * @param host
     * @param pathResourceQuery
     * @return
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

    /**
     * findHotelWeather
     * Prompts the open-mateo api to find the current weather at a given hotel's location
     * Uses the hotel's longitude and latitude
     * @param hotelId hotelId
     * @return JSONObject containing current weather information
     */
    public JsonObject findHotelWeather(String hotelId){
        Hotel h1 = findHotel(hotelId);

        StringBuilder urlString = new StringBuilder();
        urlString.append("https://api.open-meteo.com/v1/forecast?");
        String hotelLat = h1.getLatitude();
        String hotelLong = h1.getLongitude();

        urlString.append("latitude=").append(hotelLat);
        urlString.append("&");
        urlString.append("longitude=").append(hotelLong);
        urlString.append("&current_weather=true");

        System.out.println("URL: " + urlString);
        System.out.println();
        String weatherString = "";

        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;
        try {
            URL url = new URL(urlString.toString());

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?"+ url.getQuery());

            out.println(request);
            out.flush();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{")){
                    weatherString = line;

                }
                sb.append(line);
            }
        } catch (IOException e) {
            System.out.println(
                    "An IOException occured while writing to the socket stream or reading from the stream: " + e);
        } finally {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }

        JsonParser jp = new JsonParser();
        JsonObject weatherObject = jp.parse(weatherString).getAsJsonObject();
        JsonObject weather = weatherObject.getAsJsonObject("current_weather");

        return weather;
    }

    public Map<String, Hotel> getHotelMap(){
        return this.threadSafeHotelData.getHotelMap();
    }
}
