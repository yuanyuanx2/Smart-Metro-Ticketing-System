package service;

import model.Station;

import java.util.ArrayList;

/**
 * Handles station management operations.
 */
public class StationService {

    private ArrayList<Station> stations;

    /**
     * Creates an empty station list.
     */
    public StationService() {
        stations = new ArrayList<>();
    }

    /**
     * Adds a station to the station list.
     */
    public void addStation(Station station) {
        stations.add(station);
    }

    /**
     * Searches for a station by name.
     *
     * @return the matching Station, or null if not found
     */
    public Station searchStation(String name) {

        for (Station station : stations) {
            if (station.getName().equalsIgnoreCase(name)) {
                return station;
            }
        }

        return null;
    }

    /**
     * Displays all stations.
     */
    public void viewStations() {

        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }

        for (Station station : stations) {
            station.displayInfo();
            System.out.println("-------------------------");
        }
    }
}