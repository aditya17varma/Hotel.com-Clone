package jettyServer;

import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.HotelSearch;
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
import java.util.Set;

/**
 * Class KeywordSearchServlet
 * Populates the page with a list of Hotels whose name includes the given keyword
 * The hotelNames are links to their HotelInformation page
 */
public class KeywordSearchServlet extends HttpServlet {

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

            String keyword= request.getParameter("keyword");
            keyword = StringEscapeUtils.escapeHtml4(keyword);


            template = ve.getTemplate("templates/keywordSearchTemplate.html");

            JsonObject keywordJSON = new JsonObject();

            context.put("servletPath", request.getServletPath());

            if (keyword != null){
                Set<Hotel> hotelSet = hs.findHotelByKeyword(keyword);

                if (hotelSet != null){
                    keywordJSON = jsCreator.createKeywordJson(hotelSet);
                    keywordJSON.addProperty("keyword", keyword);
                    context.put("keyword", keyword);
                }
                else {
                    keywordJSON.addProperty("success", false);
                    context.put("keyword", "invalid");
                }
            }
            else {
                keywordJSON.addProperty("success", false);
                context.put("keyword", "invalid");
            }

            context.put("keywordJSON", keywordJSON);
            System.out.println(keywordJSON);

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
        String keyword = request.getParameter("keyword");
        keyword = StringEscapeUtils.escapeHtml4(keyword);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("keyword", keyword);

        HttpSession session = request.getSession();

        session.setAttribute("keyword", keyword);

        if (keyword != null){
            response.sendRedirect("/search?keyword=" + keyword);
        }
        else {
            System.out.println("no keyword");
        }
    }

}
