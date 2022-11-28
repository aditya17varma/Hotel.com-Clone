package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.HotelSearch;
import hotelapp.Review;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class JsonCreator {
    HotelSearch hs;

    public JsonCreator(HotelSearch hs){
        this.hs = hs;
    }



    public JsonObject createReviewListJson(String hotelId, int num){
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

    public JsonObject createUserReviewJson(String hotelId, int num, String user){
        JsonObject reviewJSON = new JsonObject();

        reviewJSON.addProperty("success", true);
        reviewJSON.addProperty("hotelId", hotelId);

        //todo add avg rating

        List<Review> reviews = hs.findReviews(hotelId);

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
        String city = address[1].strip().replaceAll(" ","-");
        hotelJSON.addProperty("city", city);
        hotelJSON.addProperty("state", address[2].strip());
        hotelJSON.addProperty("lat", hotel.getLatitude());
        hotelJSON.addProperty("lng", hotel.getLongitude());

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.expedia.com/");
        sb.append(city).append("-Hotels-").append(hotel.getName().replaceAll(" ", "-"))
                .append(".h").append(hotel.getId()).append(".Hotel-Information");
        hotelJSON.addProperty("expedia", sb.toString());

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
