package hotelapp;

import java.lang.reflect.Array;
import java.util.*;

/** Class Hotel */
public class Hotel {
    private String name;
    private String id;
    private String latitude;
    private String longitude;
    private String address;
    private String street;
    private String city;
    private String state;

    /**
     * Class Hotel
     * @param name name of the Hotel
     * @param id HotelID
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param address Address of the Hotel
     */
    public Hotel(String name, String id, String latitude, String longitude, String address) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        String addressSplit[] = address.split("");
        this.street = addressSplit[0].strip();
        this.city = addressSplit[1].strip().replaceAll(" ","-");
        this.state = addressSplit[2].strip();
//        this.roomMap = new HashMap<>();
//        for (int i = 1; i < 4; i++){
//            roomMap.put(i, new Integer[90]);
//            Arrays.fill(roomMap.get(i), -1);
//        }
    }

    /**
     * getName
     * @return Hotel Name
     */
    public String getName() {
        return name;
    }

    /**
     * getId
     * @return Hotel ID
     */
    public String getId() {
        return id;
    }

    /**
     * getLatitude
     * @return Latitude coordinate
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * getLongitude
     * @return Longitude coordinate
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * getAddress
     * @return Hotel address
     */
    public String getAddress() {
        return address;
    }

//    public HashMap<Integer, Integer[]> getRoomMap(){
//        return this.roomMap;
//    }
//
//    public void bookRoom(int room, String startDate, String endDate){
//        // yyyy-mm-dd
//
//        int startVal = dateToNum(startDate);
//        int endVal = dateToNum(endDate);
//
//        Integer[] roomArr = this.roomMap.get(room);
//
//        for (int i = startVal; i <= endVal; i ++){
//            roomArr[i] = 1;
//        }
//    }
//
//    public List<Integer> checkAvailability(String startDate, String endDate){
//        List<Integer> rooms = new ArrayList<>();
//
//        if (startDate.equals("start") && endDate.equals("end")){
//            rooms.add(1);
//            rooms.add(2);
//            rooms.add(3);
//            return rooms;
//        }
//
//        int start = dateToNum(startDate);
//        int end = dateToNum(endDate);
//
//        for (int roomNum: this.roomMap.keySet()){
//            Integer[] roomArr = this.roomMap.get(roomNum);
//            boolean available = true;
//            for (int i = start; i <= end; i++){
//                if (roomArr[i] == 1){
//                    available = false;
//                    break;
//                }
//            }
//            if (available){
//                rooms.add(roomNum);
//            }
//        }
//        return rooms;
//    }

    public int dateToNum(String date){
        String[] dateSplit = date.split("-");
        int dateVal = 0;
        if (dateSplit[0].equals("2022")){
            if (dateSplit[1].equals("12")){
                dateVal = Integer.parseInt(dateSplit[2]);
            }
        }
        else if (dateSplit[0].equals("2023")){
            if (dateSplit[1].equals("01")){
                dateVal = 31 + Integer.parseInt(dateSplit[2]);
            }
            else if (dateSplit[1].equals("02")){
                dateVal = 62 + Integer.parseInt(dateSplit[2]);
            }
        }

        return dateVal - 1;
    }

    /**
     * toString
     * @return String representation of this Hotel
     */
    @Override
    public String toString() {
        String[] addressSplit = address.split(",");
        return "********************" + "\n" +
                name + ": " + id +  '\n' +
                addressSplit[0] + "\n" +
                addressSplit[1].trim() + "," + addressSplit[2] + '\n';
    }

//    public static void main(String[] args) {
//        Hotel h1 = new Hotel("name", "123", "12", "21", "365 2nd Ave");
//        System.out.println(h1.roomMap.keySet());
//
//        h1.bookRoom(1, "2022-12-01", "2022-12-31");
//        h1.bookRoom(2, "2023-01-01", "2023-01-31");
//        h1.bookRoom(3, "2023-02-01", "2023-02-28");
//        for (int room: h1.roomMap.keySet()){
//            System.out.println(Arrays.toString(h1.roomMap.get(room)));
//        }
//
//        List<Integer> rooms = h1.checkAvailability("2023-02-01", "2023-02-28");
//        System.out.println(rooms);
//    }
}
