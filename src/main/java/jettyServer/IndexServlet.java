package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.HotelSearch;
import hotelapp.ReviewWordCount;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Class IndexServlet
 */
public class IndexServlet extends HttpServlet {

    /**
     * doGet
     * Processes the HttpServletRequest and responds with inverted index information
     * @param request request
     * @param response inverted index information in JSON
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");

        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");

        String word = request.getParameter("word");
        word = StringEscapeUtils.escapeHtml4(word);
        String num = request.getParameter("num");
        num = StringEscapeUtils.escapeHtml4(num);

        JsonObject indexJSON = new JsonObject();

        if (word != null && num != null){
            List<ReviewWordCount> reviews = hs.findWord(word);

            if (reviews != null){
                JsonArray result = new JsonArray();
                for (int i = 0; i < Math.min(Integer.parseInt(num), reviews.size()); i++){
                    ReviewWordCount tempReview = reviews.get(i);
                    JsonObject tempR = new JsonObject();
                    tempR.addProperty("reviewId", tempReview.getReviewID());
                    tempR.addProperty("title", tempReview.getReview().getTitle());
                    tempR.addProperty("user", tempReview.getReview().getUserNickname());
                    tempR.addProperty("reviewText", tempReview.getReview().getReviewText());
                    tempR.addProperty("date", tempReview.getReview().getDatePosted().toString());
                    result.add(tempR);
                }

                indexJSON.addProperty("success", true);
                indexJSON.addProperty("word", word);
                indexJSON.add("reviews", result);
            }
            else {
                indexJSON.addProperty("success", false);
                indexJSON.addProperty("word", "invalid");
            }
        }
        else {
            indexJSON.addProperty("success", false);
            indexJSON.addProperty("word", "invalid");
        }

        PrintWriter out = response.getWriter();

        out.println(indexJSON);

        response.setStatus(HttpServletResponse.SC_OK);

    }
}
