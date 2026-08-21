package app;

import enums.TicketStatus;
import enums.TicketType;
import exception.FileProcessingException;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.User;
import repository.FileManager;
import repository.TXTFileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandlingTest {

    public static void main(String[] args) {

        String userFile =
                "src/main/resources/data/users_test.txt";

        String stationFile =
                "src/main/resources/data/stations_test.txt";

        String routeFile =
                "src/main/resources/data/routes_test.txt";

        String ticketFile =
                "src/main/resources/data/tickets_test.txt";

        // -------------------------
        // Create original data
        // -------------------------

        Passenger passenger =
                new Passenger(
                        "P001",
                        "Ali",
                        "ali@email.com",
                        "pass123",
                        75.50
                );

        HashMap<String, User> users =
                new HashMap<>();

        users.put(
                passenger.getUserId(),
                passenger
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

        try {

            // -------------------------
            // SAVE
            // -------------------------

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
                    routes,
                    routeFile
            );

            saveManager.saveData(
                    tickets,
                    ticketFile
            );

            System.out.println(
                    "All data saved successfully."
            );

            // -------------------------
            // SIMULATE PROGRAM RESTART
            // -------------------------

            FileManager loadManager =
                    new TXTFileManager();

            Object loadedUserData =
                    loadManager.loadData(userFile);

            Object loadedStationData =
                    loadManager.loadData(stationFile);

            Object loadedRouteData =
                    loadManager.loadData(routeFile);

            Object loadedTicketData =
                    loadManager.loadData(ticketFile);

            HashMap<String, User> loadedUsers =
                    new HashMap<>();

            ArrayList<Station> loadedStations =
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

            Passenger loadedPassenger =
                    (Passenger) loadedUsers.get("P001");

            Ticket loadedTicket =
                    loadedTickets.get(0);

            System.out.println("\nLoaded ticket:");

            System.out.printf(
                    "%-6s | %-12s | %-12s -> %-12s | %-7s | %-9s | RM %.2f%n",
                    loadedTicket.getTicketId(),
                    loadedTicket.getPassenger().getName(),
                    loadedTicket.getSource().getName(),
                    loadedTicket.getDestination().getName(),
                    loadedTicket.getTicketType(),
                    loadedTicket.getStatus(),
                    loadedTicket.getFare()
            );

            System.out.println(
                    "\nObject reference check:"
            );

            boolean samePassengerObject =
                    loadedTicket.getPassenger()
                            == loadedPassenger;

            boolean sameSourceObject =
                    loadedTicket.getSource()
                            == loadedStations.get(0);

            boolean sameDestinationObject =
                    loadedTicket.getDestination()
                            == loadedStations.get(1);

            System.out.println(
                    "Passenger uses loaded object   : "
                            + samePassengerObject
            );

            System.out.println(
                    "Source uses loaded Station     : "
                            + sameSourceObject
            );

            System.out.println(
                    "Destination uses loaded Station: "
                            + sameDestinationObject
            );

            System.out.println(
                    "\nPurchase time restoration check:"
            );

            System.out.println(
                    loadedTicket.getPurchaseDateTime()
            );

            System.out.println(
                    "Purchase time preserved        : "
                            + loadedTicket
                            .getPurchaseDateTime()
                            .equals(purchaseTime)
            );

            System.out.println(
                    "\nLoaded route count: "
                            + loadedRoutes.size()
            );

        } catch (FileProcessingException e) {

            System.out.println(
                    "File processing error: "
                            + e.getMessage()
            );
        }
    }
}