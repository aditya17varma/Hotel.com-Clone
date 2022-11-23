package hotelapp;

import java.time.LocalDate;
import java.util.Comparator;

/** Class HotelReviewComparator */
public class ReviewComparator implements Comparator<Review> {
    /**
     * compare
     * @param r1 first Review object
     * @param r2 second Review object
     * @return -1 if review posted later, 1 if earlier, if same date compares the reviewID
     */
    @Override
    public int compare(Review r1, Review r2){
        LocalDate d1 = r1.getDatePosted();
        LocalDate d2 = r2.getDatePosted();

        int compareDate = d2.compareTo(d1);

        if (compareDate == 0){
            String id1 = r1.getReviewID();
            String id2 = r2.getReviewID();

            return id1.compareTo(id2);
        }
        return compareDate;
    }


}
