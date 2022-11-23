package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HotelFileParser {
    /**
     * parseHotelFile
     * @param filepath filepath of the Hotel reviews json
     * @return ArrayList of Hotel objects
     */
    public List<Hotel> parseHotelFile(String filepath) {
        List<Hotel> hotels = new ArrayList<>();
        JsonParser parser = new JsonParser();

        try (FileReader fr = new FileReader(filepath)) {
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray hotelList = jo.getAsJsonArray("sr");

            for (int i = 0; i < hotelList.size(); i++) {
                JsonObject current = hotelList.get(i).getAsJsonObject();

                String name = current.get("f").toString().replaceAll("\"", "");
                String id = current.get("id").toString().replaceAll("\"", "");
                JsonObject coordinates = current.getAsJsonObject("ll").getAsJsonObject();
                String latitude = coordinates.get("lat").toString().replaceAll("\"", "");
                String longitude = coordinates.get("lng").toString().replaceAll("\"", "");

                StringBuilder add = new StringBuilder();
                add.append(current.get("ad").toString().replaceAll("\"", "")).append(", ")
                        .append(current.get("ci").toString().replaceAll("\"", "")).append(", ")
                        .append(current.get("pr").toString().replaceAll("\"", ""));

                String address = add.toString();

                Hotel tempHotel = new Hotel(name, id, latitude, longitude, address);
                hotels.add(tempHotel);

            }

        } catch (IOException e) {
            System.out.println("Could not find filepath: " + filepath);
        }

        return hotels;
    }

}
