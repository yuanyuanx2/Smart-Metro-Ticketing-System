package repository;

import enums.TicketStatus;
import enums.TicketType;
import exception.FileProcessingException;
import model.Admin;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles saving and loading data using TXT files.
 */
public class TXTFileManager implements FileManager {

    private final HashMap<String, Station> stationLookup =
            new HashMap<>();

    private final HashMap<String, Passenger> passengerLookup =
            new HashMap<>();

    @Override
    public void saveData(Object data, String fileName)
            throws FileProcessingException {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(fileName)
                     )) {

            if (data instanceof Map<?, ?> map) {

                for (Object item :
                        map.values()) {

                    writer.write(
                            convertToText(item)
                    );

                    writer.newLine();
                }

            } else if (data
                    instanceof Iterable<?> collection) {

                for (Object item :
                        collection) {

                    writer.write(
                            convertToText(item)
                    );

                    writer.newLine();
                }

            } else {

                writer.write(
                        convertToText(data)
                );

                writer.newLine();
            }

        } catch (IOException e) {

            throw new FileProcessingException(
                    "Unable to save data to file: "
                            + fileName
            );
        }
    }

    @Override
    public Object loadData(String fileName)
            throws FileProcessingException {

        ArrayList<Object> data =
                new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(fileName)
                     )) {

            String line;

            while ((line =
                    reader.readLine()) != null) {

                data.add(
                        convertFromText(line)
                );
            }

        } catch (IOException e) {

            throw new FileProcessingException(
                    "Unable to load data from file: "
                            + fileName
            );
        }

        return data;
    }

    /**
     * Converts supported Java objects
     * into TXT records.
     */
    private String convertToText(Object item)
            throws FileProcessingException {

        if (item
                instanceof Passenger passenger) {

            return "PASSENGER|"
                    + passenger.getUserId()
                    + "|"
                    + passenger.getName()
                    + "|"
                    + passenger.getEmail()
                    + "|"
                    + passenger.getPassword()
                    + "|"
                    + passenger.getBalance();
        }

        if (item
                instanceof Admin admin) {

            return "ADMIN|"
                    + admin.getUserId()
                    + "|"
                    + admin.getName()
                    + "|"
                    + admin.getEmail()
                    + "|"
                    + admin.getPassword();
        }

        if (item
                instanceof Station station) {

            return "STATION|"
                    + station.getStationId()
                    + "|"
                    + station.getName()
                    + "|"
                    + station.getLocation();
        }

        if (item
                instanceof Train train) {

            return "TRAIN|"
                    + train.getTrainId()
                    + "|"
                    + train.getTrainName()
                    + "|"
                    + train.getCapacity();
        }

        if (item
                instanceof Route route) {

            return "ROUTE|"
                    + route.getRouteId()
                    + "|"
                    + route.getSource()
                    .getStationId()
                    + "|"
                    + route.getDestination()
                    .getStationId()
                    + "|"
                    + route.calculateDistance();
        }

        if (item
                instanceof Ticket ticket) {

            return "TICKET|"
                    + ticket.getTicketId()
                    + "|"
                    + ticket.getPassenger()
                    .getUserId()
                    + "|"
                    + ticket.getSource()
                    .getStationId()
                    + "|"
                    + ticket.getDestination()
                    .getStationId()
                    + "|"
                    + ticket.getTicketType()
                    + "|"
                    + ticket.getStatus()
                    + "|"
                    + ticket.getFare()
                    + "|"
                    + ticket.getPurchaseDateTime()
                    + "|"
                    + ticket.isLoyaltyDiscountApplied();
        }

        if (item instanceof String text) {

            return text;
        }

        throw new FileProcessingException(
                "Unsupported data type for TXT saving."
        );
    }

    /**
     * Converts TXT records back into
     * Java objects.
     */
    private Object convertFromText(String line)
            throws FileProcessingException {

        if (line.startsWith("PASSENGER|")) {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );

            if (parts.length != 6) {

                throw new FileProcessingException(
                        "Invalid passenger data: "
                                + line
                );
            }

            try {

                double balance =
                        Double.parseDouble(
                                parts[5]
                        );

                Passenger passenger =
                        new Passenger(
                                parts[1],
                                parts[2],
                                parts[3],
                                parts[4],
                                balance
                        );

                passengerLookup.put(
                        passenger.getUserId(),
                        passenger
                );

                return passenger;

            } catch (NumberFormatException e) {

                throw new FileProcessingException(
                        "Invalid passenger balance: "
                                + line
                );
            }
        }

        if (line.startsWith("ADMIN|")) {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );

            if (parts.length != 5) {

                throw new FileProcessingException(
                        "Invalid admin data: "
                                + line
                );
            }

            return new Admin(
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4]
            );
        }

        if (line.startsWith("STATION|")) {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );

            if (parts.length != 4) {

                throw new FileProcessingException(
                        "Invalid station data: "
                                + line
                );
            }

            Station station =
                    new Station(
                            parts[1],
                            parts[2],
                            parts[3]
                    );

            stationLookup.put(
                    station.getStationId(),
                    station
            );

            return station;
        }

        if (line.startsWith("TRAIN|")) {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );

            if (parts.length != 4) {

                throw new FileProcessingException(
                        "Invalid train data: "
                                + line
                );
            }

            try {

                int capacity =
                        Integer.parseInt(
                                parts[3]
                        );

                return new Train(
                        parts[1],
                        parts[2],
                        capacity
                );

            } catch (NumberFormatException e) {

                throw new FileProcessingException(
                        "Invalid train capacity: "
                                + line
                );
            }
        }

        if (line.startsWith("ROUTE|")) {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );

            if (parts.length != 5) {

                throw new FileProcessingException(
                        "Invalid route data: "
                                + line
                );
            }

            Station source =
                    stationLookup.get(
                            parts[2]
                    );

            Station destination =
                    stationLookup.get(
                            parts[3]
                    );

            if (source == null
                    || destination == null) {

                throw new FileProcessingException(
                        "Route refers to a station "
                                + "that has not been loaded: "
                                + line
                );
            }

            try {

                double distanceKm =
                        Double.parseDouble(
                                parts[4]
                        );

                return new Route(
                        parts[1],
                        source,
                        destination,
                        distanceKm
                );

            } catch (NumberFormatException e) {

                throw new FileProcessingException(
                        "Invalid route distance: "
                                + line
                );
            }
        }

        if (line.startsWith("TICKET|")) {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );

            /*
             * Backward compatibility:
             *
             * Old Ticket:
             * 9 fields
             *
             * New Ticket:
             * 10 fields including
             * loyaltyDiscountApplied.
             */
            if (parts.length != 9
                    && parts.length != 10) {

                throw new FileProcessingException(
                        "Invalid ticket data: "
                                + line
                );
            }

            Passenger passenger =
                    passengerLookup.get(
                            parts[2]
                    );

            Station source =
                    stationLookup.get(
                            parts[3]
                    );

            Station destination =
                    stationLookup.get(
                            parts[4]
                    );

            if (passenger == null) {

                throw new FileProcessingException(
                        "Ticket refers to a passenger "
                                + "that has not been loaded: "
                                + line
                );
            }

            if (source == null
                    || destination == null) {

                throw new FileProcessingException(
                        "Ticket refers to a station "
                                + "that has not been loaded: "
                                + line
                );
            }

            try {

                TicketType ticketType =
                        TicketType.valueOf(
                                parts[5]
                        );

                TicketStatus status =
                        TicketStatus.valueOf(
                                parts[6]
                        );

                double fare =
                        Double.parseDouble(
                                parts[7]
                        );

                LocalDateTime purchaseDateTime =
                        LocalDateTime.parse(
                                parts[8]
                        );

                boolean loyaltyDiscountApplied =
                        false;

                /*
                 * Old 9-field records automatically
                 * receive false.
                 */
                if (parts.length == 10) {

                    if (!parts[9]
                            .equalsIgnoreCase("true")
                            && !parts[9]
                            .equalsIgnoreCase("false")) {

                        throw new FileProcessingException(
                                "Invalid loyalty discount value: "
                                        + line
                        );
                    }

                    loyaltyDiscountApplied =
                            Boolean.parseBoolean(
                                    parts[9]
                            );
                }

                return new Ticket(
                        parts[1],
                        passenger,
                        source,
                        destination,
                        ticketType,
                        status,
                        fare,
                        purchaseDateTime,
                        loyaltyDiscountApplied
                );

            } catch (NumberFormatException
                     | DateTimeParseException e) {

                throw new FileProcessingException(
                        "Invalid ticket data: "
                                + line
                );

            } catch (IllegalArgumentException e) {

                throw new FileProcessingException(
                        "Invalid ticket data: "
                                + line
                );
            }
        }

        return line;
    }
}