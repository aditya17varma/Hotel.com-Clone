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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


/**
 * Class ModifyReviewServlet
 * Displays the Reviews by the session user and allows them to edit or delete the Reviews
 */
public class ModifyReviewServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

//        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        HttpSession session = request.getSession();
        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        StringWriter writer = new StringWriter();

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template;

        context.put("sessionName", sessionName);

        context.put("servletPath", request.getServletPath());

        if (sessionName != null){
            JsonCreator jc = new JsonCreator(dbHandler);

            String hotelId = request.getParameter("hotelId");

            hotelId = StringEscapeUtils.escapeHtml4(hotelId);

            template = ve.getTemplate("templates/modifyReviewTemplate.html");

            JsonObject infoJSON = new JsonObject();
            JsonObject hotelJSON = new JsonObject();
            JsonObject reviewJSON;

            context.put("servletPath", request.getServletPath());

            if (hotelId != null) {
                Hotel tempHotel = dbHandler.findHotel(hotelId);
//                        hs.findHotel(hotelId);
                if (tempHotel != null) {
                    hotelJSON = jc.createHotelJson(tempHotel);

                    context.put("hotelName", tempHotel.getName());
                    List<Review> reviews = dbHandler.findHotelReviews(hotelId);
//                            hs.findReviews(hotelId);

                    if (reviews != null) {
                        reviewJSON = jc.createUserReviewJson(hotelId, reviews.size(), sessionName);
                    } else {
                        reviewJSON = jc.setFailure();
                        context.put("hotelName", "invalid");
                    }
                    infoJSON.add("hotelReviews", reviewJSON);
                } else {
                    hotelJSON = jc.setFailure();
                }
                infoJSON.add("hotelData", hotelJSON);
            }
            context.put("infoJSON", infoJSON);
            session.setAttribute("infoJSON", infoJSON);

        }
        else {
            //redirect to login or register
            template = ve.getTemplate("templates/noLoginTemplate.html");
        }
        template.merge(context, writer);
        PrintWriter out = response.getWriter();
        out.println(writer.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");

        HttpSession session = request.getSession();

        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        JsonObject infoJSON = (JsonObject) session.getAttribute("infoJSON");
        JsonObject hotelJSON = infoJSON.get("hotelData").getAsJsonObject();
        String hotelId = hotelJSON.get("hotelId").getAsString();

        String hotelName = hotelJSON.get("name").getAsString();

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("hotelData", hotelJSON);
        context.put("infoJSON", infoJSON);

        session.setAttribute("hotelId", hotelId);

        if (hotelId != null){
            response.sendRedirect("/modifyReview?hotelId=" + hotelId + "&hotelName=" + hotelName);
        }
        else {
            System.out.println("cannot modify review");
            response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
        }
    }

}
