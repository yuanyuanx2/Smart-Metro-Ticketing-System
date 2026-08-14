package model;

/**
 * Represents a metro station in the Smart Metro Ticketing System.
 */
public class Station {

    private String stationId;
    private String name;
    private String location;

    /**
     * Creates a station with its ID, name and location.
     */
    public Station(String stationId, String name, String location) {
        this.stationId = stationId;
        this.name = name;
        this.location = location;
    }

    public String getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Displays the station information.
     */
    public void displayInfo() {
        System.out.println("Station ID : " + stationId);
        System.out.println("Name       : " + name);
        System.out.println("Location   : " + location);
    }
}