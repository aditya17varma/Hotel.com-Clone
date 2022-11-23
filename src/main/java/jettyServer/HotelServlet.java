package jettyServer;

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

        JsonCreator jc = new JsonCreator(hsTemp);

        PrintWriter out = response.getWriter();

        if (hotelId != null){
            Hotel tempHotel = hsTemp.findHotel(hotelId);

            if (tempHotel != null){
                hotelJSON = jc.createHotelJson(tempHotel);
            }
            else {
                hotelJSON = jc.setFailure();
            }
        }
        else {
            hotelJSON = jc.setFailure();
        }

		out.println(hotelJSON);

        response.setStatus(HttpServletResponse.SC_OK);

    }
}
