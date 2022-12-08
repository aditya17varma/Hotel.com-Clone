package jettyServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.Hotel;
import hotelapp.HotelSearch;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

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

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        JsonCreator jc = new JsonCreator(dbHandler);

        JsonObject weatherJSON = new JsonObject();

        if (hotelId != null){
            Hotel tempHotel = dbHandler.findHotel(hotelId);

            if (tempHotel != null){
                weatherJSON.addProperty("success", true);
                weatherJSON.addProperty("hotelId", hotelId);
                weatherJSON.addProperty("hotelName", tempHotel.getName());
                weatherJSON.add("current_weather", findHotelWeather(tempHotel));
            }
            else {
                weatherJSON = jc.setFailure();
            }
        }
        else {
            weatherJSON = jc.setFailure();
        }

        PrintWriter out = response.getWriter();

        out.println(weatherJSON);
        System.out.println(weatherJSON);

        response.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * Takes a host and a string containing path/resource/query and creates a
     * string of the HTTP GET request
     * @param host
     * @param pathResourceQuery
     * @return
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

    /**
     * findHotelWeather
     * Prompts the open-mateo api to find the current weather at a given hotel's location
     * Uses the hotel's longitude and latitude
     * @param hotel hotel
     * @return JSONObject containing current weather information
     */
    public JsonObject findHotelWeather(Hotel hotel){
        Hotel h1 = hotel;

        StringBuilder urlString = new StringBuilder();
        urlString.append("https://api.open-meteo.com/v1/forecast?");
        String hotelLat = h1.getLatitude();
        String hotelLong = h1.getLongitude();

        urlString.append("latitude=").append(hotelLat);
        urlString.append("&");
        urlString.append("longitude=").append(hotelLong);
        urlString.append("&current_weather=true");

        System.out.println("URL: " + urlString);
        System.out.println();
        String weatherString = "";

        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;
        try {
            URL url = new URL(urlString.toString());

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?"+ url.getQuery());

            out.println(request);
            out.flush();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{")){
                    weatherString = line;

                }
                sb.append(line);
            }
        } catch (IOException e) {
            System.out.println(
                    "An IOException occurred while writing to the socket stream or reading from the stream: " + e);
        } finally {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("An exception occurred while trying to close the streams or the socket: " + e);
            }
        }

        JsonParser jp = new JsonParser();
        JsonObject weatherObject = jp.parse(weatherString).getAsJsonObject();
        JsonObject weather = weatherObject.getAsJsonObject("current_weather");

        return weather;
    }
}
