package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.Review;

import java.util.List;


/**
 * Class JsonCreator
 * Helper class to create JSON objects for Hotels and Reviews
 */
public class JsonCreator {
    DatabaseHandler dbHandler;

    public JsonCreator(DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;
    }

    /**
     * createReviewListJson
     * Creates a JsonObject that has a JsonArray of Reviews
     * @param hotelId hotelId
     * @param num number of Reviews
     * @return JsonObject
     */
    public JsonObject createReviewListJson(String hotelId, int num){
        JsonObject reviewJSON = new JsonObject();

        reviewJSON.addProperty("success", true);
        reviewJSON.addProperty("hotelId", hotelId);

        List<Review> reviews = dbHandler.findHotelReviews(hotelId);

        JsonArray result = new JsonArray();

        for (int i = 0; i < Math.min(num, reviews.size()); i++){
            Review tempReview = reviews.get(i);
            JsonObject tempR = new JsonObject();
            tempR.addProperty("reviewId", tempReview.getReviewID());
            tempR.addProperty("title", tempReview.getTitle());
            tempR.addProperty("user", tempReview.getUserNickname());
            tempR.addProperty("reviewText", tempReview.getReviewText());
            tempR.addProperty("date", tempReview.getDatePosted().toString());
            tempR.addProperty("rating", tempReview.getRatingOverall().toString());
            result.add(tempR);
        }

        reviewJSON.add("reviews", result);

        return reviewJSON;
    }

    /**
     * createReview
     * Creates a JsonObject for the Review class
     * @param review Review
     * @return JsonObject
     */
    public JsonObject createReview(Review review){
        JsonObject reviewJSON = new JsonObject();

        reviewJSON.addProperty("success", true);
        reviewJSON.addProperty("hotelId", review.getHotelID());
        reviewJSON.addProperty("reviewId", review.getReviewID());
        reviewJSON.addProperty("rating", review.getRatingOverall());
        reviewJSON.addProperty("title", review.getTitle());
        reviewJSON.addProperty("reviewText", review.getReviewText());
        reviewJSON.addProperty("user", review.getUserNickname());
        reviewJSON.addProperty("date", review.getDatePosted().toString());

        return reviewJSON;

    }

    /**
     * createUserReviewJson
     * Creates a JsonObject that has a JsonArray of Reviews by a single User
     * @param hotelId HotelId
     * @param num number of Reviews
     * @param user User
     * @return JsonObject
     */
    public JsonObject createUserReviewJson(String hotelId, int num, String user){
        JsonObject reviewJSON = new JsonObject();

        reviewJSON.addProperty("success", true);
        reviewJSON.addProperty("hotelId", hotelId);

        List<Review> reviews = dbHandler.findHotelReviews(hotelId);

        JsonArray result = new JsonArray();

        for (int i = 0; i < Math.min(num, reviews.size()); i++){
            Review tempReview = reviews.get(i);
            if (tempReview.getUserNickname().equals(user)){
                JsonObject tempR = new JsonObject();
                tempR.addProperty("reviewId", tempReview.getReviewID());
                tempR.addProperty("title", tempReview.getTitle());
                tempR.addProperty("user", tempReview.getUserNickname());
                tempR.addProperty("reviewText", tempReview.getReviewText());
                tempR.addProperty("date", tempReview.getDatePosted().toString());
                tempR.addProperty("rating", tempReview.getRatingOverall().toString());
                result.add(tempR);
            }
        }

        reviewJSON.add("reviews", result);

        return reviewJSON;
    }

    /**
     * createKeywordJson
     * Creates a JsonObject that has a JsonArray of all Hotel whose name includes the keyword
     * @param hotelList List of Hotels that whose name inlcudes the keyword
     * @return JsonObject
     */
    public JsonObject createKeywordJson(List<Hotel> hotelList){
        JsonObject keywordJSON = new JsonObject();

        keywordJSON.addProperty("success", true);

        JsonArray hotels = new JsonArray();

        for (Hotel hotel: hotelList){
            JsonObject tempHotelJSON = createHotelJson(hotel);
            hotels.add(tempHotelJSON);
        }

        keywordJSON.add("keywordHotels", hotels);

        return keywordJSON;
    }

    /**
     * createHotelJson
     * Creates a JsonObject for Hotel
     * @param hotel Hotel
     * @return JsonObject
     */
    public JsonObject createHotelJson(Hotel hotel){
        JsonObject hotelJSON = new JsonObject();

        hotelJSON.addProperty("success", true);
        hotelJSON.addProperty("name", hotel.getName());
        hotelJSON.addProperty("hotelId", hotel.getId());
        String[] address = hotel.getAddress().split(",");
        hotelJSON.addProperty("addr", address[0].strip());
        String city = address[1].strip().replaceAll(" ","-");
        hotelJSON.addProperty("city", city);
        hotelJSON.addProperty("state", address[2].strip());
        hotelJSON.addProperty("lat", hotel.getLatitude());
        hotelJSON.addProperty("lng", hotel.getLongitude());

        String rating = dbHandler.getAvgRating(hotel.getId());
        if (rating == null){
            rating = "0";
        }

        hotelJSON.addProperty("rating", rating);

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.expedia.com/");
        sb.append(city).append("-Hotels-").append(hotel.getName().replaceAll(" ", "-"))
                .append(".h").append(hotel.getId()).append(".Hotel-Information");
        hotelJSON.addProperty("expedia", sb.toString());

        return hotelJSON;
    }


    /**
     * setFailure
     * Sets JsonObject "success" to false and "hotelId" and "HotelName" to "invalid"
     * @return JsonObject
     */
    public JsonObject setFailure(){
        JsonObject failureJSON = new JsonObject();
        failureJSON.addProperty("success", false);
        failureJSON.addProperty("hotelId", "invalid");
        failureJSON.addProperty("hotelName", "invalid");

        return failureJSON;
    }

}
