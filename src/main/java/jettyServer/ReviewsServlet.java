package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class ReviewsServlet extends HttpServlet {
    public final static int LIMIT = 5;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        HttpSession session = request.getSession();
        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        int offset;
        Object res = session.getAttribute("offset");
        if (res == null) { // loading for the first time
            offset = 0;
        }
        else {
            offset = (int)res;
        }
        session.setAttribute("offset", offset + LIMIT);


        StringWriter writer = new StringWriter();

        JsonObject infoJSON = new JsonObject();

        if (sessionName != null){
            JsonCreator jc = new JsonCreator(dbHandler);

            String hotelId = request.getParameter("hotelId");

            hotelId = StringEscapeUtils.escapeHtml4(hotelId);


            JsonObject hotelJSON = new JsonObject();
            JsonObject reviewJSON = new JsonObject();


            if (hotelId != null) {
                Hotel tempHotel = dbHandler.findHotel(hotelId);
                if (tempHotel != null) {
                    hotelJSON = jc.createHotelJson(tempHotel);

                    session.setAttribute("hotelName", tempHotel.getName());
                    List<Review> reviews = dbHandler.findHotelReviewsLimit(hotelId, LIMIT, offset);

                    if (reviews.size() > 0) {
                        JsonArray reviewArr = new JsonArray();
                        for (Review r: reviews){
                            JsonObject tempR = jc.createReview(r);
                            reviewArr.add(tempR);
                        }

                        reviewJSON.add("reviews", reviewArr);
                    }
                    else {
                        offset = 0;
                        session.setAttribute("offset", offset);

                        reviewJSON = jc.setFailure();
                        session.setAttribute("hotelName", "invalid");

                        response.sendRedirect("/reviews"+"?hotelId="+hotelId);
                        return;
                    }
                    infoJSON.add("hotelReviews", reviewJSON);
                }
                else {
                    hotelJSON = jc.setFailure();
                }
                infoJSON.add("hotelData", hotelJSON);
            }
            session.setAttribute("infoJSON", infoJSON);

        }
        else {
            //redirect to login or register
            response.sendRedirect("/login");
        }

        PrintWriter out = response.getWriter();
        out.println(infoJSON);
    }


}
