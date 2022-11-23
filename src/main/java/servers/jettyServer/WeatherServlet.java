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
 * Class WeatherServlet
 */
public class WeatherServlet extends HttpServlet{

    /**
     * doGet
     * Processes the HttpServletRequest and responds with weather information
     * @param request request
     * @param response Weather information in JSON
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");

        HotelSearch hsTemp = (HotelSearch) getServletContext().getAttribute("data");
        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);


        JsonObject weatherJSON = new JsonObject();

        if (hotelId != null){
            Hotel tempHotel = hsTemp.findHotel(hotelId);

            if (tempHotel != null){
                weatherJSON.addProperty("success", true);
                weatherJSON.addProperty("hotelId", hotelId);
                weatherJSON.addProperty("hotelName", tempHotel.getName());
                weatherJSON.add("current_weather", hsTemp.findHotelWeather(hotelId));
            }
            else {
                weatherJSON.addProperty("success", false);
                weatherJSON.addProperty("current_weather", "invalid");
            }

        }
        else {
            weatherJSON.addProperty("success", false);
            weatherJSON.addProperty("current_weather", "invalid");
        }

        PrintWriter out = response.getWriter();

        out.println(weatherJSON);

        response.setStatus(HttpServletResponse.SC_OK);

    }
}
