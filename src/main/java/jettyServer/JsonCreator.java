package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.HotelSearch;
import hotelapp.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JsonCreator {
    HotelSearch hs;

    public JsonCreator(HotelSearch hs){
        this.hs = hs;
    }



    public JsonObject createReviewJson(String hotelId, int num){
        JsonObject reviewJSON = new JsonObject();

        reviewJSON.addProperty("success", true);
        reviewJSON.addProperty("hotelId", hotelId);

        //todo add avg rating

        List<Review> reviews = hs.findReviews(hotelId);

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

    public JsonObject createKeywordJson(Set<Hotel> hotelSet){
        JsonObject keywordJSON = new JsonObject();

        keywordJSON.addProperty("success", true);

        JsonArray hotels = new JsonArray();

        for (Hotel hotel: hotelSet){
            JsonObject tempHotelJSON = createHotelJson(hotel);
            hotels.add(tempHotelJSON);
        }

        keywordJSON.add("keywordHotels", hotels);

        return keywordJSON;
    }

    public JsonObject createHotelJson(Hotel hotel){
        JsonObject hotelJSON = new JsonObject();

        hotelJSON.addProperty("success", true);
        hotelJSON.addProperty("name", hotel.getName());
        hotelJSON.addProperty("hotelId", hotel.getId());
        String[] address = hotel.getAddress().split(",");
        hotelJSON.addProperty("addr", address[0].strip());
        hotelJSON.addProperty("city", address[1].strip());
        hotelJSON.addProperty("state", address[2].strip());
        hotelJSON.addProperty("lat", hotel.getLatitude());
        hotelJSON.addProperty("lng", hotel.getLongitude());

        return hotelJSON;
    }

    public JsonObject setFailure(){
        JsonObject failureJSON = new JsonObject();
        failureJSON.addProperty("success", false);
        failureJSON.addProperty("hotelId", "invalid");
        failureJSON.addProperty("hotelName", "invalid");

        return failureJSON;

    }



}
