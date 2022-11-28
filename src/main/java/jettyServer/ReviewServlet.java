package jettyServer;

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

        JsonCreator jsCreator = new JsonCreator(hs);

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String num = request.getParameter("num");
        num = StringEscapeUtils.escapeHtml4(num);
        
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/reviewsTemplate.html");


        JsonObject reviewJSON = new JsonObject();

        if (hotelId != null && num != null){
            Hotel tempHotel = hs.findHotel(hotelId);
            String hotelName = tempHotel.getName();
            context.put("hotelName", hotelName);

            List<Review> reviews = hs.findReviews(hotelId);

            if (reviews != null){
                reviewJSON = jsCreator.createReviewListJson(hotelId, Integer.parseInt(num));
            }
            else {
                reviewJSON = jsCreator.setFailure();
                context.put("hotelName", "invalid");
            }
        }
        else {
            context.put("hotelName", "invalid");
        }

        context.put("reviewJSON", reviewJSON);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        PrintWriter out = response.getWriter();

        out.println(writer.toString());

    }
}
