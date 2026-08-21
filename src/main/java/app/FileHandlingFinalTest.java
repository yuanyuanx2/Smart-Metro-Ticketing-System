package app;

import enums.TicketStatus;
import enums.TicketType;
import exception.FileProcessingException;
import model.Admin;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import repository.FileManager;
import repository.TXTFileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandlingFinalTest {

    public static void main(String[] args) {

        String userFile =
                "src/main/resources/data/users.txt";

        String stationFile =
                "src/main/resources/data/stations.txt";

        String trainFile =
                "src/main/resources/data/trains.txt";

        String routeFile =
                "src/main/resources/data/routes.txt";

        String ticketFile =
                "src/main/resources/data/tickets.txt";

        try {

            System.out.println(
                    "========== FILE HANDLING FINAL TEST =========="
            );

            // ==========================================
            // 1. CREATE ORIGINAL DATA
            // ==========================================

            Passenger passenger =
                    new Passenger(
                            "P001",
                            "Ali",
                            "ali@email.com",
                            "pass123",
                            75.50
                    );

            Admin admin =
                    new Admin(
                            "A001",
                            "Metro Admin",
                            "admin@email.com",
                            "admin123"
                    );

            HashMap<String, User> users =
                    new HashMap<>();

            users.put(
                    passenger.getUserId(),
                    passenger
            );

            users.put(
                    admin.getUserId(),
                    admin
            );


            Station s1 =
                    new Station(
                            "S001",
                            "KL Sentral",
                            "Kuala Lumpur"
                    );

            Station s2 =
                    new Station(
                            "S002",
                            "Pasar Seni",
                            "Kuala Lumpur"
                    );

            Station s3 =
                    new Station(
                            "S003",
                            "Masjid Jamek",
                            "Kuala Lumpur"
                    );

            ArrayList<Station> stations =
                    new ArrayList<>();

            stations.add(s1);
            stations.add(s2);
            stations.add(s3);


            ArrayList<Train> trains =
                    new ArrayList<>();

            trains.add(
                    new Train(
                            "T001",
                            "Metro Express",
                            300
                    )
            );

            trains.add(
                    new Train(
                            "T002",
                            "City Line",
                            250
                    )
            );

            trains.add(
                    new Train(
                            "T003",
                            "Rapid Metro",
                            350
                    )
            );


            ArrayList<Route> routes =
                    new ArrayList<>();

            routes.add(
                    new Route(
                            "R001",
                            s1,
                            s2,
                            2.5
                    )
            );

            routes.add(
                    new Route(
                            "R002",
                            s2,
                            s3,
                            1.8
                    )
            );


            LocalDateTime purchaseTime =
                    LocalDateTime.of(
                            2026,
                            8,
                            21,
                            14,
                            30
                    );

            ArrayList<Ticket> tickets =
                    new ArrayList<>();

            tickets.add(
                    new Ticket(
                            "TKT001",
                            passenger,
                            s1,
                            s2,
                            TicketType.SINGLE,
                            TicketStatus.ACTIVE,
                            5.00,
                            purchaseTime
                    )
            );


            // ==========================================
            // 2. SAVE ALL DATA
            // ==========================================

            FileManager saveManager =
                    new TXTFileManager();

            saveManager.saveData(
                    users,
                    userFile
            );

            saveManager.saveData(
                    stations,
                    stationFile
            );

            saveManager.saveData(
                    trains,
                    trainFile
            );

            saveManager.saveData(
                    routes,
                    routeFile
            );

            saveManager.saveData(
                    tickets,
                    ticketFile
            );

            System.out.println(
                    "\n[PASS] All TXT files saved."
            );


            // ==========================================
            // 3. SIMULATE PROGRAM RESTART
            // ==========================================

            FileManager loadManager =
                    new TXTFileManager();

            // Important loading order
            Object loadedUserData =
                    loadManager.loadData(userFile);

            Object loadedStationData =
                    loadManager.loadData(stationFile);

            Object loadedTrainData =
                    loadManager.loadData(trainFile);

            Object loadedRouteData =
                    loadManager.loadData(routeFile);

            Object loadedTicketData =
                    loadManager.loadData(ticketFile);


            HashMap<String, User> loadedUsers =
                    new HashMap<>();

            ArrayList<Station> loadedStations =
                    new ArrayList<>();

            ArrayList<Train> loadedTrains =
                    new ArrayList<>();

            ArrayList<Route> loadedRoutes =
                    new ArrayList<>();

            ArrayList<Ticket> loadedTickets =
                    new ArrayList<>();


            if (loadedUserData instanceof ArrayList<?> data) {

                for (Object item : data) {

                    if (item instanceof User user) {

                        loadedUsers.put(
                                user.getUserId(),
                                user
                        );
                    }
                }
            }


            if (loadedStationData instanceof ArrayList<?> data) {

                for (Object item : data) {

                    if (item instanceof Station station) {
                        loadedStations.add(station);
                    }
                }
            }


            if (loadedTrainData instanceof ArrayList<?> data) {

                for (Object item : data) {

                    if (item instanceof Train train) {
                        loadedTrains.add(train);
                    }
                }
            }


            if (loadedRouteData instanceof ArrayList<?> data) {

                for (Object item : data) {

                    if (item instanceof Route route) {
                        loadedRoutes.add(route);
                    }
                }
            }


            if (loadedTicketData instanceof ArrayList<?> data) {

                for (Object item : data) {

                    if (item instanceof Ticket ticket) {
                        loadedTickets.add(ticket);
                    }
                }
            }


            // ==========================================
            // 4. VERIFY COLLECTIONS
            // ==========================================

            check(
                    loadedUsers.size() == 2,
                    "Users loaded"
            );

            check(
                    loadedStations.size() == 3,
                    "Stations loaded"
            );

            check(
                    loadedTrains.size() == 3,
                    "Trains loaded"
            );

            check(
                    loadedRoutes.size() == 2,
                    "Routes loaded"
            );

            check(
                    loadedTickets.size() == 1,
                    "Tickets loaded"
            );


            // ==========================================
            // 5. VERIFY USER DATA
            // ==========================================

            Passenger loadedPassenger =
                    (Passenger) loadedUsers.get("P001");

            Admin loadedAdmin =
                    (Admin) loadedUsers.get("A001");

            check(
                    loadedPassenger != null,
                    "Passenger restored"
            );

            check(
                    loadedAdmin != null,
                    "Admin restored"
            );

            check(
                    loadedPassenger.login(
                            "ali@email.com",
                            "pass123"
                    ),
                    "Passenger login restored"
            );

            check(
                    loadedAdmin.login(
                            "admin@email.com",
                            "admin123"
                    ),
                    "Admin login restored"
            );

            check(
                    loadedPassenger.getBalance() == 75.50,
                    "Passenger balance restored"
            );


            // ==========================================
            // 6. VERIFY TRAIN DATA
            // ==========================================

            Train loadedTrain =
                    loadedTrains.get(0);

            check(
                    loadedTrain.getTrainId().equals("T001"),
                    "Train ID restored"
            );

            check(
                    loadedTrain.getCapacity() == 300,
                    "Train capacity restored"
            );


            // ==========================================
            // 7. VERIFY ROUTE REFERENCES
            // ==========================================

            Route loadedRoute =
                    loadedRoutes.get(0);

            check(
                    loadedRoute.getSource()
                            == loadedStations.get(0),
                    "Route source Station reference preserved"
            );

            check(
                    loadedRoute.getDestination()
                            == loadedStations.get(1),
                    "Route destination Station reference preserved"
            );


            // ==========================================
            // 8. VERIFY TICKET DATA AND REFERENCES
            // ==========================================

            Ticket loadedTicket =
                    loadedTickets.get(0);

            check(
                    loadedTicket.getPassenger()
                            == loadedPassenger,
                    "Ticket Passenger reference preserved"
            );

            check(
                    loadedTicket.getSource()
                            == loadedStations.get(0),
                    "Ticket source Station reference preserved"
            );

            check(
                    loadedTicket.getDestination()
                            == loadedStations.get(1),
                    "Ticket destination Station reference preserved"
            );

            check(
                    loadedTicket.getTicketType()
                            == TicketType.SINGLE,
                    "Ticket type restored"
            );

            check(
                    loadedTicket.getStatus()
                            == TicketStatus.ACTIVE,
                    "Ticket status restored"
            );

            check(
                    loadedTicket.getFare() == 5.00,
                    "Ticket fare restored"
            );

            check(
                    loadedTicket
                            .getPurchaseDateTime()
                            .equals(purchaseTime),
                    "Ticket purchase time restored"
            );


            // ==========================================
            // 9. VERIFY FILE ERROR HANDLING
            // ==========================================

            System.out.println(
                    "\nTesting missing-file handling..."
            );

            try {

                loadManager.loadData(
                        "src/main/resources/data/does_not_exist.txt"
                );

                System.out.println(
                        "[FAIL] Missing file was not detected."
                );

            } catch (FileProcessingException e) {

                System.out.println(
                        "[PASS] FileProcessingException handled correctly."
                );

                System.out.println(
                        e.getMessage()
                );
            }


            // ==========================================
            // FINAL RESULT
            // ==========================================

            System.out.println(
                    "\n=============================================="
            );

            System.out.println(
                    "FILE HANDLING FINAL TEST PASSED"
            );

            System.out.println(
                    "=============================================="
            );


        } catch (FileProcessingException e) {

            System.out.println(
                    "\nFILE HANDLING FINAL TEST FAILED"
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }


    /**
     * Prints a PASS result or stops the test when
     * an expected condition is not satisfied.
     */
    private static void check(
            boolean condition,
            String description) {

        if (condition) {

            System.out.println(
                    "[PASS] " + description
            );

        } else {

            throw new IllegalStateException(
                    "[FAIL] " + description
            );
        }
    }
}