package hotelapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Class HotelData */
public class ThreadSafeHotelData extends HotelData{
    private final ReentrantReadWriteLock lock;


    /**
     * Class HotelData
     * Initializes maps for HotelMap, reviewMap, reviewWordCountMap, and InvertedIndex
     * Initializes InvertedIndex
     * Creates instance of ReentrantReadWriteLock
     */
    public ThreadSafeHotelData(){
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * loadHotels
     * Loads the hotelMap with Hotel objects
     * key: hotelID, value: hotel
     * @param hotelPath filepath for Hotel file
     */
    @Override
    public void loadHotels(String hotelPath){
        lock.writeLock().lock();
        try{
            HotelFileParser h1 = new HotelFileParser();
            List<Hotel> tempHotels = h1.parseHotelFile(hotelPath);

            for (Hotel h: tempHotels){
                super.addHotelToHotelMap(h);
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * loadReviews
     * Loads the HotelSearch Reviews hashMap with HotelReview objects
     * @param reviewPath reviewPath filepath for reviews directory
     * @param numThreads number of threads to create
     */
    public void loadReviews(String reviewPath, int numThreads){
            ThreadSafeReviewFileParser r1 = new ThreadSafeReviewFileParser(numThreads);
            r1.setThreadSafeHotelData(this);
            r1.findAndParseReviewFiles(reviewPath, false);
    }

    /**
     * combineReviews
     * locks the reviewMap and adds reviews parsed by thread
     * @param threadReviews List of reviews returned by thread
     */
    public void combineReviews(List<Review> threadReviews){
        lock.writeLock().lock();
        try{
            for (Review hr: threadReviews){
                super.addReviewToReviewMap(hr);
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * loadReviewWordCounts
     * Loads reviewWordCountMap with ReviewWordCount object for each Review
     */
    @Override
    public void loadReviewWordCounts(){
        lock.writeLock().lock();
        try {
            super.loadReviewWordCounts();
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * createInvertedIndex
     * Loads the InvertedIndex with lists of ReviewWordCount objects
     */
    @Override
    public void createInvertedIndex(){
        lock.writeLock().lock();
        try{
            super.createInvertedIndex();
        }
        finally {
            lock.writeLock().unlock();
        }
        sortInvertedIndex();
    }

    /**
     * sortInvertedIndex
     * Sorts the Lists of the InvertedIndex using the ReviewWordComparator
     * The values in the lists are sorted in descending order of word frequency
     */
    @Override
    public void sortInvertedIndex(){
        lock.writeLock().lock();
        try{
            super.sortInvertedIndex();
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * findHotel
     * @param id hotelID
     * @return Hotel object with matching ID
     */
    @Override
    public Hotel findHotel(String id){
        lock.readLock().lock();
        try{
            return super.findHotel(id);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * findReview
     * @param id HotelID
     * @return ArrayList of HotelReview objects with matching HotelID, empty ArrayList if none found
     */
    @Override
    public List<Review> findReview(String id){
        lock.readLock().lock();
        try{
            return super.findReview(id);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * getReviewWordCountArray
     * @param word word to search InvertedIndex
     * @return ArrayList of ReviewWordCount objects
     */
    @Override
    public ArrayList<ReviewWordCount> getReviewWordCountArray(String word){
        lock.readLock().lock();
        try{
            return super.getReviewWordCountArray(word);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * getHotelIDs
     * Set of all the hotelID keys in the hotelMap
     * @return Set of hotelIDs
     */
    @Override
    public Set<String> getHotelIDs(){
        lock.readLock().lock();
        try{
            return super.getHotelIDs();
        }
        finally {
            lock.readLock().unlock();
        }
    }
}
