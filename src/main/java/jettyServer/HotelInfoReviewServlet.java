package jettyServer;

import com.google.gson.JsonObject;
import hotelapp.Hotel;
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


/**
 * Class HotelInfoReviewServlet
 * Given a hotelId, populates the page with Hotel information including Hotel Name, address, Id, average rating, and a list of Reviews
 */
public class HotelInfoReviewServlet extends HttpServlet {
    private static final int DEFAULT_BUTTONS = 5;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        HttpSession session = request.getSession();
        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        StringWriter writer = new StringWriter();

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template;

        String limitString;
        if (session.getAttribute("LIMIT") != null){
            limitString = String.valueOf(session.getAttribute("LIMIT"));
            limitString = StringEscapeUtils.escapeHtml4(limitString);
        }
        else {
            limitString = "0";
        }

        int limit = Integer.parseInt(limitString);



        context.put("sessionName", sessionName);

        if (sessionName != null){
            JsonCreator jc = new JsonCreator(dbHandler);

            String hotelId = request.getParameter("hotelId");


            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            context.put("hotelId", hotelId);

        int reviewCount = dbHandler.reviewCount(hotelId);

        int numButtons;
        if (limit != 0){
            numButtons = reviewCount / limit;
            int mod = reviewCount % limit;
            if (mod > 0){
                numButtons += 1;
            }
        }
        else {
            numButtons = reviewCount / DEFAULT_BUTTONS;
            int mod = reviewCount % DEFAULT_BUTTONS;
            if (mod > 0){
                numButtons += 1;
            }
        }



        context.put("numButtons", numButtons);

            template = ve.getTemplate("templates/hotelInfoReview.html");

            JsonObject infoJSON = new JsonObject();
            JsonObject hotelJSON = new JsonObject();
            JsonObject reviewJSON;

            context.put("servletPath", request.getServletPath());

            if (hotelId != null) {
                Hotel tempHotel = dbHandler.findHotel(hotelId);
                if (tempHotel != null) {
                    hotelJSON = jc.createHotelJson(tempHotel);

                    session.setAttribute("hotelName", tempHotel.getName());
                    context.put("hotelName", tempHotel.getName());
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

}
