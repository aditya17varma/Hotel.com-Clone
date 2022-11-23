package servers.jettyServer;

import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.HotelSearch;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class HotelServlet
 */
public class HotelServlet extends HttpServlet {

    /**
     * doGet
     * Processes the HttpServletRequest and responds with hotel information
     * @param request request
     * @param response Hotel information in JSON
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");

        HotelSearch hsTemp = (HotelSearch) getServletContext().getAttribute("data");
        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        JsonObject hotelJSON = new JsonObject();

        PrintWriter out = response.getWriter();

        if (hotelId != null){
            Hotel tempHotel = hsTemp.findHotel(hotelId);


            if (tempHotel != null){
                hotelJSON.addProperty("success", true);
                hotelJSON.addProperty("hotelId", tempHotel.getId());
                hotelJSON.addProperty("name", tempHotel.getName());
                String[] address = tempHotel.getAddress().split(",");
                hotelJSON.addProperty("addr", address[0].strip());
                hotelJSON.addProperty("city", address[1].strip());
                hotelJSON.addProperty("state", address[2].strip());
                hotelJSON.addProperty("lat", tempHotel.getLatitude());
                hotelJSON.addProperty("lng", tempHotel.getLongitude());

            }
            else {
                hotelJSON.addProperty("success", false);
                hotelJSON.addProperty("hotelId", "invalid");
            }
        }
        else {
            hotelJSON.addProperty("success", false);
            hotelJSON.addProperty("hotelId", "invalid");
        }

		out.println(hotelJSON);

        response.setStatus(HttpServletResponse.SC_OK);

    }
}
