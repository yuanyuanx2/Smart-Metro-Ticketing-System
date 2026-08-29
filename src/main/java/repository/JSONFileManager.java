package repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.TicketStatus;
import enums.TicketType;
import exception.FileProcessingException;
import model.Admin;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Bonus file manager that saves and loads
 * Smart Metro Ticketing System data in JSON format.
 *
 * TXT remains the main lecturer-required persistence format.
 */
public class JSONFileManager implements FileManager {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    /*
     * Lookup collections rebuild object relationships
     * when Routes and Tickets are loaded.
     */
    private final HashMap<String, Station> stationLookup =
            new HashMap<>();

    private final HashMap<String, Passenger> passengerLookup =
            new HashMap<>();

    /**
     * Saves supported system data as a JSON array.
     */
    @Override
    public void saveData(
            Object data,
            String fileName)
            throws FileProcessingException {

        ArrayNode jsonArray =
                objectMapper.createArrayNode();

        if (data instanceof Map<?, ?> map) {

            for (Object item :
                    map.values()) {

                jsonArray.add(
                        convertToJson(
                                item
                        )
                );
            }

        } else if (data
                instanceof Iterable<?> collection) {

            for (Object item :
                    collection) {

                jsonArray.add(
                        convertToJson(
                                item
                        )
                );
            }

        } else {

            jsonArray.add(
                    convertToJson(
                            data
                    )
            );
        }

        try {

            Path path =
                    Paths.get(
                            fileName
                    );

            Path parent =
                    path.getParent();

            if (parent != null) {

                Files.createDirectories(
                        parent
                );
            }

            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(
                            path.toFile(),
                            jsonArray
                    );

        } catch (IOException e) {

            throw new FileProcessingException(
                    "Unable to save JSON data to file: "
                            + fileName
            );
        }
    }

    /**
     * Loads a JSON array and rebuilds
     * supported Java objects.
     */
    @Override
    public Object loadData(
            String fileName)
            throws FileProcessingException {

        ArrayList<Object> data =
                new ArrayList<>();

        try {

            JsonNode root =
                    objectMapper.readTree(
                            Paths.get(fileName)
                                    .toFile()
                    );

            if (root == null) {
                return data;
            }

            if (!root.isArray()) {

                throw new FileProcessingException(
                        "Invalid JSON file format: "
                                + fileName
                );
            }

            for (JsonNode node :
                    root) {

                data.add(
                        convertFromJson(
                                node
                        )
                );
            }

        } catch (IOException e) {

            throw new FileProcessingException(
                    "Unable to load JSON data from file: "
                            + fileName
            );
        }

        return data;
    }

    /**
     * Converts a supported Java object
     * into one JSON record.
     */
    private ObjectNode convertToJson(
            Object item)
            throws FileProcessingException {

        ObjectNode json =
                objectMapper.createObjectNode();

        if (item
                instanceof Passenger passenger) {

            json.put(
                    "recordType",
                    "PASSENGER"
            );

            json.put(
                    "userId",
                    passenger.getUserId()
            );

            json.put(
                    "name",
                    passenger.getName()
            );

            json.put(
                    "email",
                    passenger.getEmail()
            );

            json.put(
                    "password",
                    passenger.getPassword()
            );

            json.put(
                    "balance",
                    passenger.getBalance()
            );

            return json;
        }

        if (item
                instanceof Admin admin) {

            json.put(
                    "recordType",
                    "ADMIN"
            );

            json.put(
                    "userId",
                    admin.getUserId()
            );

            json.put(
                    "name",
                    admin.getName()
            );

            json.put(
                    "email",
                    admin.getEmail()
            );

            json.put(
                    "password",
                    admin.getPassword()
            );

            return json;
        }

        if (item
                instanceof Station station) {

            json.put(
                    "recordType",
                    "STATION"
            );

            json.put(
                    "stationId",
                    station.getStationId()
            );

            json.put(
                    "name",
                    station.getName()
            );

            json.put(
                    "location",
                    station.getLocation()
            );

            return json;
        }

        if (item
                instanceof Train train) {

            json.put(
                    "recordType",
                    "TRAIN"
            );

            json.put(
                    "trainId",
                    train.getTrainId()
            );

            json.put(
                    "trainName",
                    train.getTrainName()
            );

            json.put(
                    "capacity",
                    train.getCapacity()
            );

            return json;
        }

        if (item
                instanceof Route route) {

            json.put(
                    "recordType",
                    "ROUTE"
            );

            json.put(
                    "routeId",
                    route.getRouteId()
            );

            json.put(
                    "sourceStationId",
                    route.getSource()
                            .getStationId()
            );

            json.put(
                    "destinationStationId",
                    route.getDestination()
                            .getStationId()
            );

            json.put(
                    "distanceKm",
                    route.calculateDistance()
            );

            return json;
        }

        if (item
                instanceof Ticket ticket) {

            json.put(
                    "recordType",
                    "TICKET"
            );

            json.put(
                    "ticketId",
                    ticket.getTicketId()
            );

            json.put(
                    "passengerId",
                    ticket.getPassenger()
                            .getUserId()
            );

            json.put(
                    "sourceStationId",
                    ticket.getSource()
                            .getStationId()
            );

            json.put(
                    "destinationStationId",
                    ticket.getDestination()
                            .getStationId()
            );

            json.put(
                    "ticketType",
                    ticket.getTicketType()
                            .name()
            );

            json.put(
                    "status",
                    ticket.getStatus()
                            .name()
            );

            json.put(
                    "fare",
                    ticket.getFare()
            );

            json.put(
                    "purchaseDateTime",
                    ticket.getPurchaseDateTime()
                            .toString()
            );

            /*
             * Bonus loyalty-discount information.
             */
            json.put(
                    "loyaltyDiscountApplied",
                    ticket.isLoyaltyDiscountApplied()
            );

            return json;
        }

        if (item instanceof String text) {

            json.put(
                    "recordType",
                    "TEXT"
            );

            json.put(
                    "value",
                    text
            );

            return json;
        }

        throw new FileProcessingException(
                "Unsupported data type for JSON saving."
        );
    }

    /**
     * Converts one JSON record back
     * into the corresponding Java object.
     */
    private Object convertFromJson(
            JsonNode json)
            throws FileProcessingException {

        String recordType =
                getRequiredText(
                        json,
                        "recordType"
                );

        switch (recordType) {

            case "PASSENGER":

                return createPassenger(
                        json
                );

            case "ADMIN":

                return createAdmin(
                        json
                );

            case "STATION":

                return createStation(
                        json
                );

            case "TRAIN":

                return createTrain(
                        json
                );

            case "ROUTE":

                return createRoute(
                        json
                );

            case "TICKET":

                return createTicket(
                        json
                );

            case "TEXT":

                return getRequiredText(
                        json,
                        "value"
                );

            default:

                throw new FileProcessingException(
                        "Unsupported JSON record type: "
                                + recordType
                );
        }
    }

    /**
     * Rebuilds Passenger data.
     */
    private Passenger createPassenger(
            JsonNode json)
            throws FileProcessingException {

        double balance =
                getRequiredDouble(
                        json,
                        "balance"
                );

        Passenger passenger =
                new Passenger(
                        getRequiredText(
                                json,
                                "userId"
                        ),
                        getRequiredText(
                                json,
                                "name"
                        ),
                        getRequiredText(
                                json,
                                "email"
                        ),
                        getRequiredText(
                                json,
                                "password"
                        ),
                        balance
                );

        passengerLookup.put(
                passenger.getUserId(),
                passenger
        );

        return passenger;
    }

    /**
     * Rebuilds Admin data.
     */
    private Admin createAdmin(
            JsonNode json)
            throws FileProcessingException {

        return new Admin(
                getRequiredText(
                        json,
                        "userId"
                ),
                getRequiredText(
                        json,
                        "name"
                ),
                getRequiredText(
                        json,
                        "email"
                ),
                getRequiredText(
                        json,
                        "password"
                )
        );
    }

    /**
     * Rebuilds Station data.
     */
    private Station createStation(
            JsonNode json)
            throws FileProcessingException {

        Station station =
                new Station(
                        getRequiredText(
                                json,
                                "stationId"
                        ),
                        getRequiredText(
                                json,
                                "name"
                        ),
                        getRequiredText(
                                json,
                                "location"
                        )
                );

        stationLookup.put(
                station.getStationId(),
                station
        );

        return station;
    }

    /**
     * Rebuilds Train data.
     */
    private Train createTrain(
            JsonNode json)
            throws FileProcessingException {

        return new Train(
                getRequiredText(
                        json,
                        "trainId"
                ),
                getRequiredText(
                        json,
                        "trainName"
                ),
                getRequiredInt(
                        json,
                        "capacity"
                )
        );
    }

    /**
     * Rebuilds Route data using
     * previously loaded Station objects.
     */
    private Route createRoute(
            JsonNode json)
            throws FileProcessingException {

        String sourceStationId =
                getRequiredText(
                        json,
                        "sourceStationId"
                );

        String destinationStationId =
                getRequiredText(
                        json,
                        "destinationStationId"
                );

        Station source =
                stationLookup.get(
                        sourceStationId
                );

        Station destination =
                stationLookup.get(
                        destinationStationId
                );

        if (source == null
                || destination == null) {

            throw new FileProcessingException(
                    "JSON route refers to a station "
                            + "that has not been loaded."
            );
        }

        return new Route(
                getRequiredText(
                        json,
                        "routeId"
                ),
                source,
                destination,
                getRequiredDouble(
                        json,
                        "distanceKm"
                )
        );
    }

    /**
     * Rebuilds Ticket data using
     * previously loaded Passenger
     * and Station objects.
     */
    private Ticket createTicket(
            JsonNode json)
            throws FileProcessingException {

        String passengerId =
                getRequiredText(
                        json,
                        "passengerId"
                );

        String sourceStationId =
                getRequiredText(
                        json,
                        "sourceStationId"
                );

        String destinationStationId =
                getRequiredText(
                        json,
                        "destinationStationId"
                );

        Passenger passenger =
                passengerLookup.get(
                        passengerId
                );

        Station source =
                stationLookup.get(
                        sourceStationId
                );

        Station destination =
                stationLookup.get(
                        destinationStationId
                );

        if (passenger == null) {

            throw new FileProcessingException(
                    "JSON ticket refers to a passenger "
                            + "that has not been loaded."
            );
        }

        if (source == null
                || destination == null) {

            throw new FileProcessingException(
                    "JSON ticket refers to a station "
                            + "that has not been loaded."
            );
        }

        try {

            TicketType ticketType =
                    TicketType.valueOf(
                            getRequiredText(
                                    json,
                                    "ticketType"
                            )
                    );

            TicketStatus status =
                    TicketStatus.valueOf(
                            getRequiredText(
                                    json,
                                    "status"
                            )
                    );

            LocalDateTime purchaseDateTime =
                    LocalDateTime.parse(
                            getRequiredText(
                                    json,
                                    "purchaseDateTime"
                            )
                    );

            /*
             * Old JSON backups do not contain
             * this field, so false is used as
             * the backward-compatible default.
             */
            boolean loyaltyDiscountApplied =
                    getOptionalBoolean(
                            json,
                            "loyaltyDiscountApplied",
                            false
                    );

            return new Ticket(
                    getRequiredText(
                            json,
                            "ticketId"
                    ),
                    passenger,
                    source,
                    destination,
                    ticketType,
                    status,
                    getRequiredDouble(
                            json,
                            "fare"
                    ),
                    purchaseDateTime,
                    loyaltyDiscountApplied
            );

        } catch (IllegalArgumentException
                 | DateTimeParseException e) {

            throw new FileProcessingException(
                    "Invalid JSON ticket data."
            );
        }
    }

    /**
     * Reads one required String property.
     */
    private String getRequiredText(
            JsonNode json,
            String fieldName)
            throws FileProcessingException {

        JsonNode value =
                json.get(
                        fieldName
                );

        if (value == null
                || value.isNull()
                || !value.isTextual()) {

            throw new FileProcessingException(
                    "Missing or invalid JSON field: "
                            + fieldName
            );
        }

        return value.asText();
    }

    /**
     * Reads one required integer property.
     */
    private int getRequiredInt(
            JsonNode json,
            String fieldName)
            throws FileProcessingException {

        JsonNode value =
                json.get(
                        fieldName
                );

        if (value == null
                || value.isNull()
                || !value.isIntegralNumber()) {

            throw new FileProcessingException(
                    "Missing or invalid JSON field: "
                            + fieldName
            );
        }

        return value.asInt();
    }

    /**
     * Reads one required numeric property.
     */
    private double getRequiredDouble(
            JsonNode json,
            String fieldName)
            throws FileProcessingException {

        JsonNode value =
                json.get(
                        fieldName
                );

        if (value == null
                || value.isNull()
                || !value.isNumber()) {

            throw new FileProcessingException(
                    "Missing or invalid JSON field: "
                            + fieldName
            );
        }

        return value.asDouble();
    }

    /**
     * Reads an optional boolean property.
     *
     * This is used for backward compatibility
     * with JSON files created before the
     * loyalty-discount feature existed.
     */
    private boolean getOptionalBoolean(
            JsonNode json,
            String fieldName,
            boolean defaultValue)
            throws FileProcessingException {

        JsonNode value =
                json.get(
                        fieldName
                );

        if (value == null
                || value.isNull()) {

            return defaultValue;
        }

        if (!value.isBoolean()) {

            throw new FileProcessingException(
                    "Invalid JSON field: "
                            + fieldName
            );
        }

        return value.asBoolean();
    }
}