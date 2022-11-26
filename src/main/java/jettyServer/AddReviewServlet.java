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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

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

        //todo reinstate session check
        if (sessionName != null){
            JsonCreator jsCreator = new JsonCreator(hs);

            String hotelId= request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);

            String hotelName = request.getParameter("hotelName");
            hotelName = StringEscapeUtils.escapeHtml4(hotelName);

            String userName = sessionName;

            template = ve.getTemplate("templates/addReviewTemplate.html");

            JsonObject keywordJSON = new JsonObject();

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
        String title = request.getParameter("title");
        String reviewText = request.getParameter("reviewText");
        String username = sessionName;
        String date = dtf.format(now).toString();

        Review newAddition = new Review(hotelId,reviewId, rating, title, reviewText,
                username, date);

        System.out.println(newAddition);

        if (rating != null && title != null && reviewText != null){
            hs.addReview(newAddition);
        }


        if (rating != null && title != null && reviewText != null){
            response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
        }

        else if (hotelId != null){
            response.sendRedirect("/addReview?hotelId=" + hotelId + "&hotelName=" + hotelName + "$username=" + username);
        }
        else {
            System.out.println("cannot add review");
            response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
        }
    }

    public String generateReviewId(HotelSearch hs){
        UUID id = UUID.randomUUID();
        while (hs.checkReviewIds(id.toString().replaceAll("-",""))){
            id = UUID.randomUUID();
        }
        return id.toString().replaceAll("-","");
    }
}
