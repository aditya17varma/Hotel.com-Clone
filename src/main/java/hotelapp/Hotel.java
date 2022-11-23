package hotelapp;

/** Class Hotel */
public class Hotel {
    private String name;
    private String id;
    private String latitude;
    private String longitude;
    private String address;

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
}
