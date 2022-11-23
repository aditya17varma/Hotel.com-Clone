package servers.jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.HotelSearch;
import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ReviewServlet
 */
public class ReviewServlet extends HttpServlet {

    /**
     * doGet
     * Processes the HttpServletRequest and responds with hotel review information
     * @param request request
     * @param response Review information in JSON
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        response.setContentType("application/json");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String num = request.getParameter("num");
        num = StringEscapeUtils.escapeHtml4(num);

        Hotel tempHotel = hs.findHotel(hotelId);

        String hotelName = tempHotel.getName();

        JsonObject reviewJSON = new JsonObject();

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/reviewsTemplate.html");
        context.put("hotelName", hotelName);

        ArrayList<Review> reviewsResult = new ArrayList<>();

        if (hotelId != null && num != null){
            List<Review> reviews = hs.findReviews(hotelId);

            if (reviews != null){
                JsonArray result = new JsonArray();

                for (int i = 0; i < Math.min(Integer.parseInt(num), reviews.size()); i++){
                    reviewsResult.add(reviews.get(i));

                    Review tempReview = reviews.get(i);
                    JsonObject tempR = new JsonObject();
                    tempR.addProperty("reviewId", tempReview.getReviewID());
                    tempR.addProperty("title", tempReview.getTitle());
                    tempR.addProperty("user", tempReview.getUserNickname());
                    tempR.addProperty("reviewText", tempReview.getReviewText());
                    tempR.addProperty("date", tempReview.getDatePosted().toString());
                    result.add(tempR);
                }

                reviewJSON.addProperty("success", true);
                reviewJSON.addProperty("hotelId", hotelId);
                reviewJSON.add("reviews", result);
            }
            else {
                reviewJSON.addProperty("success", false);
                reviewJSON.addProperty("hotelId", "invalid");
            }
        }
        else {
            reviewJSON.addProperty("success", false);
            reviewJSON.addProperty("hotelId", "invalid");
        }

        System.out.println(reviewsResult);
        context.put("reviews", reviewsResult);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        PrintWriter out = response.getWriter();

//        out.println(reviewJSON);
        out.println(writer.toString());

    }
}
