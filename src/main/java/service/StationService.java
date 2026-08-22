package service;

import model.Station;

import java.util.ArrayList;
import java.util.Comparator;

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
     * Adds a Station to the station list.
     *
     * Station IDs and names must be unique.
     */
    public void addStation(Station station) {

        if (station == null) {

            throw new IllegalArgumentException(
                    "Station cannot be null."
            );
        }

        for (Station existingStation : stations) {

            if (existingStation.getStationId()
                    .equalsIgnoreCase(
                            station.getStationId()
                    )) {

                throw new IllegalArgumentException(
                        "Station ID already exists: "
                                + station.getStationId()
                );
            }

            if (existingStation.getName()
                    .equalsIgnoreCase(
                            station.getName()
                    )) {

                throw new IllegalArgumentException(
                        "Station name already exists: "
                                + station.getName()
                );
            }
        }

        stations.add(
                station
        );
    }

    /**
     * Searches for a Station by name.
     *
     * @return matching Station, or null if not found
     */
    public Station searchStation(String name) {

        for (Station station : stations) {

            if (station.getName()
                    .equalsIgnoreCase(name)) {

                return station;
            }
        }

        return null;
    }

    /**
     * Sorts Stations alphabetically
     * by station name.
     */
    public void sortStationsByName() {

        Comparator<Station> nameComparator =
                Comparator.comparing(
                        Station::getName,
                        String.CASE_INSENSITIVE_ORDER
                );

        stations.sort(
                nameComparator
        );
    }

    /**
     * Returns a copy of the current Station list
     * for persistence and controlled use.
     */
    public ArrayList<Station> getStations() {

        return new ArrayList<>(
                stations
        );
    }

    /**
     * Displays all Stations.
     */
    public void viewStations() {

        if (stations.isEmpty()) {

            System.out.println(
                    "No stations available."
            );

            return;
        }

        for (Station station : stations) {

            station.displayInfo();

            System.out.println(
                    "-------------------------"
            );
        }
    }
}