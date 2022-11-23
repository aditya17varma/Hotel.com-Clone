package hotelapp;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReviewWordCount {
    private LocalDate date;
    private String reviewID;
    private Map<String, Integer> freqDist;
    private Review review;

    /**
     * Class ReviewWordCount
     * Holds the reviewID, word count, and LocalDate of the corresponding Review
     * @param review
     */

    public ReviewWordCount(Review review) {
        this.reviewID = review.getReviewID();
        this.freqDist = new HashMap<>();
        this.date = review.getDatePosted();
        this.review = review;

        String[] splitReview = review.getReviewText().split(" ");
        for (String word: splitReview){
            String cleanWord = word.replaceAll("\\p{Punct}", "").toLowerCase();
            if (!freqDist.containsKey(cleanWord)){
                freqDist.put(cleanWord, 1);
            }
            else {
                freqDist.put(cleanWord, freqDist.get(cleanWord) + 1);
            }
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public String getReviewID() {
        return reviewID;
    }

    /**
     * getWordCount
     * return the count of the word in the review text
     * @param word word
     * @return count
     */
    public int getWordCount(String word){
        return this.freqDist.get(word);
    }

    /**
     * getFreqDist
     * Map of word in review text to their frequency
     * @return Map
     */
    public Map<String, Integer> getFreqDist(){
        return this.freqDist;
    }

    /**
     * getReview
     * Review associated with this ReviewWordCount object
     * @return Review
     */
    public Review getReview(){
        return this.review;
    }


}
