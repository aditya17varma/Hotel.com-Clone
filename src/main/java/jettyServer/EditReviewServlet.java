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

/**
 * Class EditReviewServlet
 * Allows a user to edit reviews posted by them
 * Allows edit of review Title, review Text, and review Rating
 */
public class EditReviewServlet extends HttpServlet {
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

        context.put("sessionName", sessionName);

        if (sessionName != null){
            JsonCreator jc = new JsonCreator(dbHandler);

            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            String reviewId = request.getParameter("reviewId");
            reviewId = StringEscapeUtils.escapeHtml4(reviewId);

            template = ve.getTemplate("templates/editReviewTemplate.html");

            JsonObject infoJSON = new JsonObject();
            JsonObject hotelJSON = new JsonObject();
            JsonObject reviewJSON = new JsonObject();

            context.put("servletPath", request.getServletPath());

            if (hotelId != null && reviewId != null) {

                Hotel tempHotel = dbHandler.findHotel(hotelId);
                if (tempHotel != null) {
                    hotelJSON = jc.createHotelJson(tempHotel);

                    context.put("hotelName", tempHotel.getName());
                    context.put("hotelId", hotelId);
                    Review tempReview = dbHandler.findReview(reviewId);
                    if (tempReview != null){
                        reviewJSON = jc.createReview(tempReview);
                        context.put("reviewId", tempReview.getReviewID());
                    }
                    else {
                        reviewJSON = jc.setFailure();
                    }
                } else {
                    hotelJSON = jc.setFailure();
                }
                infoJSON.add("hotelData", hotelJSON);
                infoJSON.add("reviewData", reviewJSON);
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
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        HttpSession session = request.getSession();

        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        JsonObject infoJSON = (JsonObject) session.getAttribute("infoJSON");
        JsonObject hotelJSON = infoJSON.get("hotelData").getAsJsonObject();

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String reviewId = request.getParameter("reviewId");
        reviewId = StringEscapeUtils.escapeHtml4(reviewId);

        String editTitle = request.getParameter("editTitle");
        editTitle = StringEscapeUtils.escapeHtml4(editTitle);
        String editText = request.getParameter("editText");
        editText = StringEscapeUtils.escapeHtml4(editText);
        String editRating = request.getParameter("editRating");
        editRating = StringEscapeUtils.escapeHtml4(editRating);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        if (editText != null || editTitle != null || editRating != null){
            Review tempR = dbHandler.findReview(reviewId);

            if (editText != null && !editText.equals("")){
                dbHandler.editReview(tempR, null, editText, null);
            }
            if (editTitle != null && !editTitle.equals("")){
                dbHandler.editReview(tempR, editTitle, null, null);
            }
            if (editRating != null && !editRating.equals("")){
                dbHandler.editReview(tempR, null, null, editRating);
            }
            response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
        }
        else if (hotelId != null && reviewId != null){
            response.sendRedirect("/editReview?hotelId=" + hotelId + "&reviewId=" + reviewId);
        }
        else {
            System.out.println("cannot edit review");
            response.sendRedirect("/editReview?hotelId=" + hotelId);
        }
    }
}
