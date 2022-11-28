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
import java.util.Set;

public class HotelInfoReviewServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");

        HttpSession session = request.getSession();
        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        StringWriter writer = new StringWriter();

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template;

        //todo reinstate session check
        if (sessionName != null){
            JsonCreator jc = new JsonCreator(hs);

            String hotelId = request.getParameter("hotelId");

            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            System.out.println("HotelID: " + hotelId);

            template = ve.getTemplate("templates/hotelInfoReview.html");

            JsonObject infoJSON = new JsonObject();
            JsonObject hotelJSON = new JsonObject();
            JsonObject reviewJSON;

            context.put("servletPath", request.getServletPath());

            if (hotelId != null) {
                Hotel tempHotel = hs.findHotel(hotelId);
                if (tempHotel != null) {
                    hotelJSON = jc.createHotelJson(tempHotel);

                    context.put("hotelName", tempHotel.getName());
                    List<Review> reviews = hs.findReviews(hotelId);

                    if (reviews != null) {
                        reviewJSON = jc.createReviewJson(hotelId, reviews.size());
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
            //todo redirect template that lets you search for hotel info
            context.put("infoJSON", infoJSON);
            session.setAttribute("infoJSON", infoJSON);
            System.out.println(session.getAttribute("infoJSON"));

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