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
 * Class HomepageServlet
 * Landing page for the user after login
 * Links to Hotel Search and Logout
 */
public class HomepageServlet extends HttpServlet {

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

        String lastLogin = (String) session.getAttribute("lastLogin");
        lastLogin = StringEscapeUtils.escapeHtml4(lastLogin);

        context.put("sessionName", sessionName);
        context.put("lastLogin", lastLogin);

        if (sessionName != null){
            template = ve.getTemplate("templates/homepage.html");

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
