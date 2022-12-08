package jettyServer;

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

public class UpdateExpediaServlet extends HttpServlet {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        response.sendRedirect("/login");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        HttpSession session = request.getSession();

        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);

        StringWriter writer = new StringWriter();
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template;

        if (sessionName != null){
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);

            dbHandler.insertExpedia(hotelId, hotelName, sessionName, date);
            response.sendRedirect("/hotelInfoReview?hotelId="+hotelId);
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
