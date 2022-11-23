package hotelapp;

import java.util.*;

public class HotelData {
    private Map<String, Hotel> hotelMap;
    private Map<String, List<Review>> reviewMap;
    private Map<String, ReviewWordCount> reviewWordCountMap;
    private InvertedIndex invertedIndex;
    private HashMap<String, Set<Hotel>> hotelKeywordMap;

    /**
     * Class HotelData
     * Initializes maps for HotelMap, reviewMap, reviewWordCountMap, and InvertedIndex
     * Initializes InvertedIndex
     * Creates instance of ReentrantReadWriteLock
     */
    public HotelData(){
        this.hotelMap = new TreeMap<>((s1, s2) -> s1.compareTo(s2));
        this.reviewMap = new HashMap<>();
        this.reviewWordCountMap = new HashMap<>();
        this.invertedIndex = new InvertedIndex();
        this.hotelKeywordMap = new HashMap<>();
    }

    /**
     * loadHotels
     * Loads the hotelMap with Hotel objects
     * key: hotelID, value: hotel
     * @param hotelPath filepath for Hotel file
     */
    public void loadHotels(String hotelPath){
        HotelFileParser h1 = new HotelFileParser();
        List<Hotel> tempHotels = h1.parseHotelFile(hotelPath);

        for (Hotel h: tempHotels){
            hotelMap.put(h.getId(), h);
        }
    }

    /**
     * addHotelToHotelMap
     * adds Hotel to the hotelMap, maps hotelId to Hotel
     * @param h
     */
    public void addHotelToHotelMap(Hotel h){
        this.hotelMap.put(h.getId(), h);
    }

    /**
     * loadReviews
     * Loads the HotelSearch Reviews hashMap with HotelReview objects
     * @param reviewPath filepath for reviews directory
     */
    public void loadReviews(String reviewPath){
        ReviewFileParser r1 = new ReviewFileParser();
        List<Review> tempReviews = r1.findAndParseReviewFiles(reviewPath);
        Set<String> reviewSet = new HashSet<>();

        Comparator<Review> reviewComp = new ReviewComparator();

        for (Review hr: tempReviews){
            if (!reviewSet.contains(hr.getReviewID())){
                if (!this.reviewMap.containsKey(hr.getHotelID())){
                    List<Review> temp = new ArrayList<>();
                    this.reviewMap.put(hr.getHotelID(), temp);
                }
                List<Review> temp = this.reviewMap.get(hr.getHotelID());
                temp.add(hr);
                reviewSet.add(hr.getReviewID());
            }
        }

        for (String id: this.reviewMap.keySet()){
            List<Review> temp = this.reviewMap.get(id);
            temp.sort(reviewComp.reversed());
        }
    }

    /**
     * addReviewToReviewMap
     * adds a review to the reviewMap, mapping hotelId to review
     * @param r Review
     */
    public void addReviewToReviewMap(Review r){
        if (!this.reviewMap.containsKey(r.getHotelID())){
            List<Review> temp = new ArrayList<>();
            this.reviewMap.put(r.getHotelID(), temp);
        }
        List<Review> temp = this.reviewMap.get(r.getHotelID());
        temp.add(r);
    }

    /**
     * sortReviewMap
     * Sorts the reviewMap using the ReviewComparator
     */
    public void sortReviewMap(){
        Comparator<Review> reviewComp = new ReviewComparator();

        for (String id: reviewMap.keySet()){
            List<Review> temp = this.reviewMap.get(id);
            temp.sort(reviewComp);
        }
    }

    /**
     * loadReviewWordCounts
     * Populates the reviewWordCount map with ReviewWordCount objects
     */
    public void loadReviewWordCounts(){
        Set<String> keys = this.reviewMap.keySet();
        for (String hID: keys){
            List<Review> tempReviewList = this.reviewMap.get(hID);
            for (Review r: tempReviewList){
                ReviewWordCount rwc = new ReviewWordCount(r);
                this.reviewWordCountMap.put(r.getReviewID(), rwc);
            }
        }
    }

    /**
     * createInvertedIndex
     * Creates an inverted index mapping words to reviews they appear in
     */
    public void createInvertedIndex(){
        for (String rID: this.reviewWordCountMap.keySet()){
            ReviewWordCount tempRWC = this.reviewWordCountMap.get(rID);
            invertedIndex.addWordRWC(tempRWC);
        }
    }

    /**
     * sortInvertedIndex
     * Sorts the List of Reviews mapped to words based on the word frequency in the Review
     */
    public void sortInvertedIndex(){
        for (String word: this.invertedIndex.getKeys()){
            ReviewWordComparator rwcComp = new ReviewWordComparator(word);
            ArrayList<ReviewWordCount> tempArray = this.invertedIndex.getReviewWordCounts(word);
            Collections.sort(tempArray, rwcComp.reversed());
        }
    }

    /**
     * createHotelKeywordMap
     * Maps words in Hotel titles to a Set of Hotel objects
     */
    public void createHotelKeywordMap(){
        for (String hotelId: this.hotelMap.keySet()){
            Hotel tempHotel = this.hotelMap.get(hotelId);
            String tempName = tempHotel.getName();
            String[] nameSplit = tempName.split(" ");
            for (String word: nameSplit){
                if (!this.hotelKeywordMap.containsKey(word)){
                    Set<Hotel> tempSet = new HashSet<>();
                    this.hotelKeywordMap.put(word, tempSet);
                }
                Set<Hotel> wordSet = hotelKeywordMap.get(word);
                wordSet.add(tempHotel);
            }
        }
    }

    /**
     * findHotelByKeyword
     * @param word keyword
     * @return Set of Hotels with keyword in title
     */
    public Set<Hotel> findHotelByKeyword(String word){
        return hotelKeywordMap.get(word);
    }

    /**
     * findHotel
     * @param id hotelID
     * @return Hotel object with matching ID
     */
    public Hotel findHotel(String id){
        return hotelMap.get(id);
    }

    /**
     * findReview
     * @param id HotelID
     * @return ArrayList of HotelReview objects with matching HotelID, empty ArrayList if none found
     */
    public List<Review> findReview(String id){
        return reviewMap.get(id);
    }

    /**
     * getReviewWordCountArray
     * @param word word to search InvertedIndex
     * @return ArrayList of ReviewWordCount objects
     */
    public ArrayList<ReviewWordCount> getReviewWordCountArray(String word){
        return this.invertedIndex.getReviewWordCounts(word);
    }

    /**
     * getHotelIDs
     * Set of all the hotelID keys in the hotelMap
     * @return Set of hotelIDs
     */
    public Set<String> getHotelIDs(){
            return this.hotelMap.keySet();
    }

    public Map<String, List<Review>> getReviewMap(){
        return this.reviewMap;
    }

    public static void main(String[] args) {
        HotelData hd = new HotelData();
        hd.loadHotels("input/hotels/hotels.json");
        hd.createHotelKeywordMap();
        System.out.println(hd.hotelKeywordMap.get("Hilton"));
    }

}
