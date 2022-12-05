package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/** Class ThreadSafeReviewFileParser */
public class MultiThreadReviewFileParser {
    private ExecutorService executor;
//    private Logger logger = (Logger) LogManager.getLogger();
    private Phaser phaser;
    private static ThreadSafeHotelData threadSafeHotelData;

    /**
     * Class ThreadSafeReviewFileParser
     * @param numThreads num of threads to create in pool
     */
    public MultiThreadReviewFileParser(int numThreads){
        executor = Executors.newFixedThreadPool(numThreads);
        phaser = new Phaser();
    }

    public void setThreadSafeHotelData(ThreadSafeHotelData tshd){
        threadSafeHotelData = tshd;
    }

    /**
     * FileWorker
     * worker thread that parses json files and adds to the list of reviews
     */
    private class FileWorker implements Runnable{
        Path dir;
        List<Review> threadReviewList = new ArrayList<>();
        JsonParser parser = new JsonParser();

        FileWorker(Path dir){
            this.dir = dir;
        }


        @Override
        public void run() {
            try (FileReader fr = new FileReader(dir.toString())){

                JsonObject jo = (JsonObject) parser.parse(fr);

                JsonObject reviewObj = jo.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection");
                JsonArray reviewList = reviewObj.getAsJsonArray("review");

                for (int i = 0; i < reviewList.size(); i++) {
                    JsonObject current = reviewList.get(i).getAsJsonObject();

                    String hotelID = current.get("hotelId").getAsString();
                    String reviewID = current.get("reviewId").getAsString();
                    String ratingOverall = current.get("ratingOverall").getAsString();
                    String titleTemp = current.get("title").getAsString();
                    String reviewtemp = current.get("reviewText").getAsString();
                    String userNickname = current.get("userNickname").getAsString();
                    if (userNickname.equals("")) {
                        userNickname = "Anonymous";
                    }
                    String datePosted = current.get("reviewSubmissionTime").getAsString();

                    Review tempHR = new Review(hotelID, reviewID, ratingOverall, titleTemp, reviewtemp, userNickname, datePosted);

                    threadReviewList.add(tempHR);
                }
                combineReviews(threadReviewList);
//                logger.debug("Worker is done processing " + dir + " added " + threadReviewList.size() + " reviews");
            }

            catch (IOException e){
                System.out.println("Could not find filepath: " + dir);
            }

            finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /**
     * combineReviews
     * combines Reviews parsed in individual threads to the mainReviewList
     * @param threadReviews List of Reviews in thread
     */
    public synchronized void combineReviews(List<Review> threadReviews) {
        threadSafeHotelData.combineReviews(threadReviews);
    }

    /**
     * findAndParseReviewFiles
     * Searches the directory for json review files
     * Recursively searches and sub-directories
     * Calls parseReviewFile on each json file
     * @param directory reviews directory
     * @return ArrayList of HotelReview objects
     */
    public void findAndParseReviewFiles(String directory, boolean recursion){
        boolean recursiveStep = recursion;

        Path p = Paths.get(directory);
        try (DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(p)){
            for (Path path: pathsInDir){
                if (!Files.isDirectory(path) && (path.toString().endsWith("json"))){
                    FileWorker worker = new FileWorker(path);
//                    logger.debug("Created a worker for " + path);
                    phaser.register();
                    executor.submit(worker);
                }
                else {
                   findAndParseReviewFiles(path.toString(), true);
                }
            }
        } catch (IOException e){
            System.out.println("Could not open directory: " + directory);
        }
        finally {
            phaser.arriveAndDeregister();
        }

        if (!recursiveStep){
            int phase = phaser.getPhase();
            phaser.awaitAdvance(phase);
            shutdownExecutor();
        }
        return;
    }

    /**
     * shutdownExecutor
     * Shuts down the ExecutorService
     */
    public void shutdownExecutor() {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

}
