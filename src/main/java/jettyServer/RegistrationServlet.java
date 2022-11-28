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


/** An example that demonstrates how to process HTML forms with servlets.
 */
@SuppressWarnings("serial")
public class RegistrationServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println();
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();
		String sessionName = (String) session.getAttribute("username");
		sessionName = StringEscapeUtils.escapeHtml4(sessionName);

		if (sessionName != null){
			response.sendRedirect("/login");
		}
		else {
			VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
			VelocityContext context = new VelocityContext();
			Template template = ve.getTemplate("templates/registrationTemplate.html");

			context.put("servletPath", request.getServletPath());

			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			out.println(writer.toString());
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String usernameParam = request.getParameter("name");
		System.out.println(usernameParam);
		usernameParam = StringEscapeUtils.escapeHtml4(usernameParam);
		String password = request.getParameter("pass");
		System.out.println(password);
		password = StringEscapeUtils.escapeHtml4(password);

		DatabaseHandler dbHandler = DatabaseHandler.getInstance();
		String registrationCheck = dbHandler.registerUser(usernameParam, password);

		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();

		Template template = ve.getTemplate("templates/postRegistration.html");

		context.put("user", usernameParam);
		context.put("registrationCheck", registrationCheck);

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		response.getWriter().println(writer.toString());
	}

}