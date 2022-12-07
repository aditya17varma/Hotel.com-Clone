package jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
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
import java.util.ArrayList;
import java.util.List;

public class ExpediaLinksServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        HttpSession session = request.getSession();
        String sessionName = (String) session.getAttribute("username");
        sessionName = StringEscapeUtils.escapeHtml4(sessionName);

        JsonCreator jc = new JsonCreator(dbHandler);

        StringWriter writer = new StringWriter();

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template;

        context.put("sessionName", sessionName);

        JsonObject expediaJSON = new JsonObject();

        context.put("servletPath", request.getServletPath());

        if (sessionName != null){
            template = ve.getTemplate("templates/expediaLinksTemplate.html");
            JsonArray linksArray = dbHandler.findExpediaLinks(sessionName);
            context.put("linksArray", linksArray);
            if (linksArray.size() > 0){
                JsonArray hotelArr = new JsonArray();
                for (JsonElement je: linksArray){
                    JsonObject jo = je.getAsJsonObject();
                    System.out.println(jo);
                    String tempId = jo.get("hotelId").getAsString();
                    System.out.println(tempId);
                    String date = jo.get("dateVisited").getAsString();
                    System.out.println(date);
                    Hotel tempHotel = dbHandler.findHotel(tempId);
                    JsonObject expediaHotel = jc.createHotelJson(tempHotel);
                    expediaHotel.addProperty("dateVisited", date);
                    hotelArr.add(expediaHotel);
                }

                expediaJSON.add("hotelsArr", hotelArr);
                expediaJSON.addProperty("success", true);
            }
            else{
                expediaJSON = jc.setFailure();
            }

            context.put("expediaJSON", expediaJSON);
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
