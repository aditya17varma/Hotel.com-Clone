package jettyServer;

import com.google.gson.JsonObject;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Class AddReviewServlet
 * Provides text fields to add review Title, review Text and review Rating
 * Session user is set as the review Author
 * Current date is set as the review Date
 */
public class AddReviewServlet extends HttpServlet {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        if (sessionName != null){
            JsonCreator jsCreator = new JsonCreator(hs);

            String hotelId= request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);

            String hotelName = request.getParameter("hotelName");
            hotelName = StringEscapeUtils.escapeHtml4(hotelName);

            String userName = sessionName;

            template = ve.getTemplate("templates/addReviewTemplate.html");

            context.put("servletPath", request.getServletPath());

            if (hotelId == null){
                response.sendRedirect("/search");
            }

            context.put("hotelId", hotelId);
            context.put("hotelName", hotelName);
            context.put("username", userName);

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
        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");

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

        session.setAttribute("hotelId", hotelId);

        LocalDateTime now = LocalDateTime.now();

        String reviewId = generateReviewId(hs);
        String rating = request.getParameter("rating");
        rating = StringEscapeUtils.escapeHtml4(rating);
        String title = request.getParameter("title");
        title = StringEscapeUtils.escapeHtml4(title);
        String reviewText = request.getParameter("reviewText");
        reviewText = StringEscapeUtils.escapeHtml4(reviewText);
        String username = sessionName;
        String date = dtf.format(now);

        Review newAddition = new Review(hotelId,reviewId, rating, title, reviewText,
                username, date);

        if (rating != null && !rating.equals("")
                && title != null && !title.equals("")
                && reviewText != null && !reviewId.equals("")){
            hs.addReview(newAddition);
            response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
        }

        else if (hotelId != null){
            response.sendRedirect("/addReview?hotelId=" + hotelId + "&hotelName=" + hotelName + "&username=" + username);
        }
        else {
            System.out.println("cannot add review");
            response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
        }
    }

    /**
     * generateReviewId
     * @param hs HotelSearch
     * @return reviewId
     */
    public String generateReviewId(HotelSearch hs){
        UUID id = UUID.randomUUID();
        while (hs.checkReviewIds(id.toString().replaceAll("-",""))){
            id = UUID.randomUUID();
        }
        return id.toString().replaceAll("-","");
    }
}
