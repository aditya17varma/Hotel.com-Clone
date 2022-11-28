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

public class DeleteReviewServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        response.sendRedirect("/login");
    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");

        HttpSession session = request.getSession();


        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        StringWriter writer = new StringWriter();
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template;

        if (sessionName != null){
            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            String reviewId = request.getParameter("reviewId");
            reviewId = StringEscapeUtils.escapeHtml4(reviewId);

            if (hotelId != null && reviewId != null){
                Review tempR = hs.findReview(hotelId, reviewId);
                if (tempR != null){
                    hs.deleteReview(hotelId, reviewId);
                    response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
                }
            }
            else {
                System.out.println("cannot delete review");
                response.sendRedirect("/hotelInfoReview?hotelId=" + hotelId);
            }
        }
        else {
            //redirect to login or register
            template = ve.getTemplate("templates/noLoginTemplate.html");
            template.merge(context, writer);
        }

        PrintWriter out = response.getWriter();

        out.println(writer.toString());


    }


}
