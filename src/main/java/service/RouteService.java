package service;

import model.Route;
import model.Station;

import java.util.ArrayList;

/**
 * Handles route management operations.
 */
public class RouteService {

    private ArrayList<Route> routes;

    /**
     * Creates an empty route list.
     */
    public RouteService() {
        routes = new ArrayList<>();
    }

    /**
     * Adds a route to the route list.
     */
    public void addRoute(Route route) {
        routes.add(route);
    }

    /**
     * Finds a route using its source and destination stations.
     *
     * @return the matching Route, or null if not found
     */
    public Route findRoute(Station source, Station destination) {

        for (Route route : routes) {
            if (route.getSource().equals(source)
                    && route.getDestination().equals(destination)) {

                return route;
            }
        }

        return null;
    }

    /**
     * Displays all available routes.
     */
    public void viewRoutes() {

        if (routes.isEmpty()) {
            System.out.println("No routes available.");
            return;
        }

        for (Route route : routes) {
            route.displayRoute();
            System.out.println("-------------------------");
        }
    }
}