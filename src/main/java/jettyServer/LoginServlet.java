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

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		username = StringEscapeUtils.escapeHtml4(username);

		HttpSession session = request.getSession();

		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		context.put("session", session);

		String sessionName = (String) session.getAttribute("username");
		sessionName = StringEscapeUtils.escapeHtml4(sessionName);

		context.put("servletPath", request.getServletPath());

		StringWriter writer = new StringWriter();

		//todo password check
		if ((sessionName != null && sessionName.equals(username)) || (username == null && sessionName != null)){
			context.put("userCheck", true);
			context.put("username", sessionName);
			Template template = ve.getTemplate("templates/postLogin.html");
			template.merge(context, writer);
		}

		else  {
			// already logged in or not logged in
			Template template = ve.getTemplate("templates/loginTemplate.html");
			template.merge(context, writer);
		}

		out.println(writer.toString());
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("username");
		String pass = request.getParameter("pass");


		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		context.put("username", user);

		HttpSession session = request.getSession();



		DatabaseHandler dbHandler = DatabaseHandler.getInstance();
		boolean flag = dbHandler.authenticateUser(user, pass);
		if (flag) {
			session.setAttribute("username", user);
			session.setAttribute("password", pass);
			response.sendRedirect("/login?username=" + user);
		}
		else
			System.out.println("Could not authenticate");
//			session.setAttribute("username", user);
			response.sendRedirect("/login");
	}
}