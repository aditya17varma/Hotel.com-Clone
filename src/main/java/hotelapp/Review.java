package hotelapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Class Review */
public class Review {
    private String hotelID;
    private String reviewID;
    private String ratingOverall;
    private String title;
    private String reviewText;
    private String userNickname;
    private LocalDate datePosted;

    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Class HotelReview
     * @param hotelID hotelID
     * @param reviewID reviewID
     * @param ratingOverall overall rating
     * @param title review title
     * @param reviewText review text
     * @param userNickname user nickname
     * @param datePosted date review was posted
     */
    public Review(String hotelID, String reviewID, String ratingOverall,
                       String title, String reviewText, String userNickname,
                       String datePosted) {
        this.hotelID = hotelID;
        this.reviewID = reviewID;
        this.ratingOverall = ratingOverall;
        this.title = title;
        this.reviewText = reviewText;
        this.userNickname = userNickname;
        this.datePosted = LocalDate.parse(datePosted.split("T")[0]);
    }

    /**
     * getHotelID
     * @return hotelID
     */
    public String getHotelID() {
        return hotelID;
    }

    /**
     * getReviewID
     * @return reviewID
     */
    public String getReviewID() {
        return reviewID;
    }

    /**
     * getRatingOverall
     * @return overall rating
     */
    public String getRatingOverall() {
        return ratingOverall;
    }

    /**
     * getTitle
     * @return review title
     */
    public String getTitle() {
        return title;
    }

    /**
     * getReviewText
     * @return review text
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * getUserNickname
     * @return user nickname
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * getDatePosted
     * @return date review was posted
     */
    public LocalDate getDatePosted() {
        return datePosted;
    }

    public void setReviewText(String text){
        this.reviewText = text;
    }

    public void setReviewTitle(String title){
        this.title = title;
    }

    public void setReviewRating(String rating){
        this.ratingOverall = rating;
    }

    /**
     * toString
     * @return string representation of the HotelReview
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Review by ").append(userNickname).append(" on ").append(datePosted.toString()).append("\r\n");
        sb.append("Rating: ").append(ratingOverall).append("\r\n");
        sb.append("ReviewId: ").append(reviewID).append("\r\n");
        if (title != null){
            sb.append(title).append("\r\n");
        }
        sb.append(reviewText).append("\r\n");

        return sb.toString();
    }
}
