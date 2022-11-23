package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Class ReviewFileParser */
public class ReviewFileParser {

    /**
     * parseReviewFile
     * Parses json file for Hotel reviews
     * @param filepath filepath to parse for reviews
     * @return ArrayList of HotelReview objects
     */
    public List<Review> parseReviewFile(String filepath){
        List<Review> reviews = new ArrayList<>();
        JsonParser parser = new JsonParser();

        try (FileReader fr = new FileReader(filepath)){
            JsonObject jo = (JsonObject) parser.parse(fr);

            JsonObject reviewObj = jo.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection");
            JsonArray reviewList = reviewObj.getAsJsonArray("review");

            for (int i = 0; i < reviewList.size(); i++){
                JsonObject current = reviewList.get(i).getAsJsonObject();

                String hotelID = current.get("hotelId").getAsString();
                String reviewID = current.get("reviewId").getAsString();
                String ratingOverall = current.get("ratingOverall").getAsString();
                String title = current.get("title").getAsString();
                String reviewtemp = current.get("reviewText").getAsString();
                String userNickname = current.get("userNickname").getAsString();
                String datePosted = current.get("reviewSubmissionTime").getAsString();

                Review tempHR = new Review(hotelID, reviewID, ratingOverall, title, reviewtemp, userNickname, datePosted);

                reviews.add(tempHR);

            }

        } catch (IOException e){
            System.out.println("Could not find filepath: " + filepath);
        }
        return reviews;
    }

    /**
     * findAndParseReviewFiles
     * Searches the directory for json review files
     * Recursively searches and sub-directories
     * Calls parseReviewFile on each json file
     * @param directory reviews directory
     * @return ArrayList of HotelReview objects
     */
    public List<Review> findAndParseReviewFiles(String directory){
        List<Review> hotels = new ArrayList<>();

        Path p = Paths.get(directory);
        try (DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(p)){
            for (Path path: pathsInDir){
                if (!Files.isDirectory(path) && (path.toString().endsWith("json"))){
                    hotels.addAll(parseReviewFile(path.toString()));
                }
                else {
                    hotels.addAll(findAndParseReviewFiles(path.toString()));
                }
            }
        } catch (IOException e){
            System.out.println("Could not open directory: " + directory);
        }

        return hotels;
    }
}
