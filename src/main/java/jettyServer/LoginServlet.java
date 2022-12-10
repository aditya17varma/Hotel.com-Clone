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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("serial")
/**
 * Class LoginServlet
 * Allows the user to login with an already registered username and password
 * Queries the database to check the username and password match
 */
public class LoginServlet extends HttpServlet {
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		username = StringEscapeUtils.escapeHtml4(username);

		String loginFailure = request.getParameter("loginFailure");
		loginFailure = StringEscapeUtils.escapeHtml4(loginFailure);

		HttpSession session = request.getSession();

		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		context.put("session", session);

		String sessionName = (String) session.getAttribute("username");
		sessionName = StringEscapeUtils.escapeHtml4(sessionName);

		context.put("servletPath", request.getServletPath());

		StringWriter writer = new StringWriter();

		System.out.println("Get user: " + username);
		System.out.println("Get Session: " + sessionName);

		if (loginFailure != null && loginFailure.equals("true")){
			//login failure, display alert
			context.put("loginFailure", true);
			context.put("username", sessionName);
			Template template = ve.getTemplate("templates/loginTemplate.html");
			template.merge(context, writer);
		}

		else if (sessionName != null){
			response.sendRedirect("/home");
		}
		else {
			// first page
			Template template = ve.getTemplate("templates/loginTemplate.html");
			template.merge(context, writer);
		}

		out.println(writer.toString());
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("username");
		String pass = request.getParameter("pass");
		System.out.println(user);
		System.out.println(pass);


		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		context.put("username", user);

		HttpSession session = request.getSession();

		DatabaseHandler dbHandler = DatabaseHandler.getInstance();

		LocalDateTime now = LocalDateTime.now();
		String dtNow = dtf.format(now);

		String lastLogin = dbHandler.findLogin(user);
		if (lastLogin.equals("")){
			//no prev login
			dbHandler.insertLogin(user, dtNow);
			System.out.println("Inserting Login: " + dtNow);
		}
		else {
			//prev login, update to current
			dbHandler.updateLogin(user, dtNow);
			System.out.println("Updated Login: " + dtNow);
		}

		boolean flag = dbHandler.authenticateUser(user, pass);
		if (flag) {
			session.setAttribute("lastLogin", lastLogin);
			session.setAttribute("currentLogin", dtNow);
			session.setAttribute("username", user);
			session.setAttribute("password", pass);
			response.sendRedirect("/login?username=" + user);
		}
		else
			System.out.println("Could not authenticate");
			context.put("loginFailure", true);
			response.sendRedirect("/login?loginFailure=true");
	}
}