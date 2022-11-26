package jettyServer;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogoutServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        session.setAttribute("username", null);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        StringWriter writer = new StringWriter();

        Template template = ve.getTemplate("templates/logoutTemplate.html");
        template.merge(context, writer);

        out.println(writer.toString());
    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("username", "invalid");

        HttpSession session = request.getSession();

        session.setAttribute("username", null);
        session.setAttribute("password", null);

        response.sendRedirect("/logout");
    }

}
