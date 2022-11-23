package jettyServer;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		Template template = ve.getTemplate("templates/registrationTemplate.html");

		context.put("servletPath", request.getServletPath());

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		out.println(writer.toString());

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
		dbHandler.registerUser(usernameParam, password);

		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		Template template = ve.getTemplate("templates/postRegistration.html");

		context.put("servletPath", request.getServletPath());
		context.put("user", usernameParam);

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		response.getWriter().println(writer.toString());

//		try {
//			Thread.sleep(1000);
//			response.sendRedirect("/login");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("Enter your username:<br><input type=\"text\" name=\"name\"><br>");
		out.printf("Enter your password:<br><input type=\"password\" name=\"pass\"><br>");
		out.printf("<p><input type=\"submit\" value=\"Enter\"></p>\n%n");
		out.printf("</form>\n%n");
	}

}