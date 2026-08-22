package service;

import exception.FileProcessingException;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import repository.FileManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles JSON backup creation and verification.
 *
 * TXT remains the primary lecturer-required
 * persistence mechanism.
 *
 * JSON is used as an additional backup format.
 */
public class JSONBackupService {

    private static final String JSON_DIRECTORY =
            "src/main/resources/data/json/";

    private static final String USERS_JSON =
            JSON_DIRECTORY + "users.json";

    private static final String STATIONS_JSON =
            JSON_DIRECTORY + "stations.json";

    private static final String TRAINS_JSON =
            JSON_DIRECTORY + "trains.json";

    private static final String ROUTES_JSON =
            JSON_DIRECTORY + "routes.json";

    private static final String TICKETS_JSON =
            JSON_DIRECTORY + "tickets.json";

    /*
     * Uses the FileManager interface so the service
     * depends on an abstraction rather than directly
     * depending on JSONFileManager.
     */
    private final FileManager fileManager;

    /**
     * Creates a JSON backup service using
     * the supplied FileManager implementation.
     */
    public JSONBackupService(
            FileManager fileManager) {

        if (fileManager == null) {

            throw new IllegalArgumentException(
                    "File manager cannot be null."
            );
        }

        this.fileManager =
                fileManager;
    }

    /**
     * Saves the complete current system state
     * into JSON backup files.
     *
     * The same logical relationship order used by
     * the TXT persistence system is maintained.
     */
    public void createBackup(
            HashMap<String, User> users,
            ArrayList<Station> stations,
            ArrayList<Train> trains,
            ArrayList<Route> routes,
            ArrayList<Ticket> tickets)
            throws FileProcessingException {

        /*
         * Save independent/master data first.
         */
        fileManager.saveData(
                users,
                USERS_JSON
        );

        fileManager.saveData(
                stations,
                STATIONS_JSON
        );

        fileManager.saveData(
                trains,
                TRAINS_JSON
        );

        /*
         * Routes depend on Stations.
         */
        fileManager.saveData(
                routes,
                ROUTES_JSON
        );

        /*
         * Tickets depend on Passengers
         * and Stations.
         */
        fileManager.saveData(
                tickets,
                TICKETS_JSON
        );
    }

    /**
     * Loads the JSON backup again and verifies
     * that it can reconstruct the saved system data.
     *
     * Loading must follow the relationship order:
     *
     * 1. Users
     * 2. Stations
     * 3. Trains
     * 4. Routes
     * 5. Tickets
     */
    public boolean verifyBackup(
            int expectedUserCount,
            int expectedStationCount,
            int expectedTrainCount,
            int expectedRouteCount,
            int expectedTicketCount)
            throws FileProcessingException {

        Object loadedUsers =
                fileManager.loadData(
                        USERS_JSON
                );

        Object loadedStations =
                fileManager.loadData(
                        STATIONS_JSON
                );

        Object loadedTrains =
                fileManager.loadData(
                        TRAINS_JSON
                );

        Object loadedRoutes =
                fileManager.loadData(
                        ROUTES_JSON
                );

        Object loadedTickets =
                fileManager.loadData(
                        TICKETS_JSON
                );

        /*
         * Validate both object type and record count.
         *
         * Route/Ticket loading itself also proves
         * that Passenger/Station relationships were
         * successfully reconstructed.
         */
        return isValidCollection(
                loadedUsers,
                User.class,
                expectedUserCount
        )
                && isValidCollection(
                loadedStations,
                Station.class,
                expectedStationCount
        )
                && isValidCollection(
                loadedTrains,
                Train.class,
                expectedTrainCount
        )
                && isValidCollection(
                loadedRoutes,
                Route.class,
                expectedRouteCount
        )
                && isValidCollection(
                loadedTickets,
                Ticket.class,
                expectedTicketCount
        );
    }

    /**
     * Checks that loaded data is an ArrayList,
     * contains only the expected type,
     * and contains the expected number of records.
     */
    private boolean isValidCollection(
            Object loadedData,
            Class<?> expectedType,
            int expectedCount) {

        if (!(loadedData
                instanceof ArrayList<?> collection)) {

            return false;
        }

        if (collection.size()
                != expectedCount) {

            return false;
        }

        for (Object item :
                collection) {

            if (!expectedType.isInstance(
                    item
            )) {

                return false;
            }
        }

        return true;
    }

    /**
     * Returns the folder used for JSON backups.
     */
    public String getBackupDirectory() {

        return JSON_DIRECTORY;
    }
}