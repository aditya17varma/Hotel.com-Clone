package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.HotelSearch;
import hotelapp.Review;

import java.util.List;

public class JsonCreator {
    HotelSearch hs;

    public JsonCreator(HotelSearch hs){
        this.hs = hs;
    }



    public JsonObject createReviewJson(String hotelId, int num){
        JsonObject reviewJSON = new JsonObject();

        reviewJSON.addProperty("success", true);
        reviewJSON.addProperty("hotelId", hotelId);

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

    public JsonObject setFailure(){
        JsonObject failureJSON = new JsonObject();
        failureJSON.addProperty("success", false);
        failureJSON.addProperty("hotelId", "invalid");
        failureJSON.addProperty("hotelName", "invalid");

        return failureJSON;

    }



}
