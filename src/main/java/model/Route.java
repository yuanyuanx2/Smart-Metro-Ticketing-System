package model;

/**
 * Represents a route between two metro stations.
 */
public class Route {

    private String routeId;
    private Station source;
    private Station destination;
    private double distanceKm;

    /**
     * Creates a route between a source and destination station.
     */
    public Route(String routeId, Station source,
                 Station destination, double distanceKm) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
    }

    /**
     * Returns the route ID.
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Returns the route distance in kilometres.
     */
    public double calculateDistance() {
        return distanceKm;
    }

    /**
     * Returns the source station.
     */
    public Station getSource() {
        return source;
    }

    /**
     * Returns the destination station.
     */
    public Station getDestination() {
        return destination;
    }

    /**
     * Displays the route information.
     */
    public void displayRoute() {
        System.out.println("Route ID    : " + routeId);
        System.out.println("Source      : " + source.getName());
        System.out.println("Destination : " + destination.getName());
        System.out.println("Distance    : " + distanceKm + " km");
    }
}