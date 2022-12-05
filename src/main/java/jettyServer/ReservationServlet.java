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
import java.util.ArrayList;
import java.util.List;

public class ReservationServlet extends HttpServlet {

//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        response.setContentType("text/html");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");
//
//
//        HttpSession session = request.getSession();
//        String sessionName = (String) session.getAttribute("username");
//        sessionName = StringEscapeUtils.escapeHtml4(sessionName);
//
//        StringWriter writer = new StringWriter();
//
//        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
//        VelocityContext context = new VelocityContext();
//        Template template;
//
//        if (sessionName != null){
//            JsonCreator jsCreator = new JsonCreator(hs);
//
//            String hotelId= request.getParameter("hotelId");
//            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
//
//            String hotelName = request.getParameter("hotelName");
//            hotelName = StringEscapeUtils.escapeHtml4(hotelName);
//
//            String userName = sessionName;
//
//            template = ve.getTemplate("templates/reservationTemplate.html");
//
//            context.put("servletPath", request.getServletPath());
//
//            if (hotelId == null){
//                response.sendRedirect("/search");
//            }
//
//            context.put("hotelId", hotelId);
//            context.put("hotelName", hotelName);
//            context.put("username", userName);
//
//        }
//        else {
//            //redirect to login or register
//            template = ve.getTemplate("templates/noLoginTemplate.html");
//        }
//
//        template.merge(context, writer);
//
//        PrintWriter out = response.getWriter();
//
//        out.println(writer.toString());
//    }
//
//
//    @Override
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HotelSearch hs = (HotelSearch) getServletContext().getAttribute("data");
//
//        HttpSession session = request.getSession();
//
//        String sessionName = (String) session.getAttribute("username");
//        sessionName = StringEscapeUtils.escapeHtml4(sessionName);
//
//        String startDate = request.getParameter("startDate");
//        startDate = StringEscapeUtils.escapeHtml4(startDate);
//
//        String endDate = request.getParameter("endDate");
//        endDate = StringEscapeUtils.escapeHtml4(endDate);
//
//        JsonObject infoJSON = (JsonObject) session.getAttribute("infoJSON");
//        JsonObject hotelJSON = infoJSON.get("hotelData").getAsJsonObject();
//        String hotelId = hotelJSON.get("hotelId").getAsString();
//
//        String hotelName = hotelJSON.get("name").getAsString();
//
//        List<Integer> availableRooms;
//
//        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
//        VelocityContext context = new VelocityContext();
//        context.put("hotelData", hotelJSON);
//
//        if (hotelId != null){
//            Hotel tempHotel = hs.findHotel(hotelId);
//
//            if (startDate == null || endDate == null){
//                availableRooms = new ArrayList<>();
//                availableRooms.add(1);
//                availableRooms.add(2);
//                availableRooms.add(3);
//            }
//            else{
//                availableRooms = tempHotel.checkAvailability(startDate, endDate);
//            }
//            context.put("availableRooms", availableRooms.size());
//        }
//        else {
//            System.out.println("cannot check reservation");
//            response.sendRedirect("/search");
//        }
//        session.setAttribute("hotelId", hotelId);
//    }




}
