package service;

import model.Route;
import model.Station;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Handles route management operations.
 */
public class RouteService {

    private ArrayList<Route> routes;

    /**
     * Creates an empty RouteService.
     */
    public RouteService() {

        this(
                new ArrayList<>()
        );
    }

    /**
     * Creates a RouteService using
     * an existing shared Route collection.
     */
    public RouteService(
            ArrayList<Route> routes) {

        if (routes == null) {

            this.routes =
                    new ArrayList<>();

        } else {

            this.routes =
                    routes;
        }
    }

    /**
     * Adds a Route.
     *
     * Route ID must be unique.
     * The same directional
     * source-destination pair
     * cannot be added twice.
     */
    public void addRoute(
            Route route) {

        if (route == null) {

            throw new IllegalArgumentException(
                    "Route cannot be null."
            );
        }

        for (Route existingRoute :
                routes) {

            if (existingRoute
                    .getRouteId()
                    .equalsIgnoreCase(
                            route.getRouteId()
                    )) {

                throw new IllegalArgumentException(
                        "Route ID already exists: "
                                + route.getRouteId()
                );
            }

            boolean sameSource =
                    existingRoute.getSource()
                            == route.getSource();

            boolean sameDestination =
                    existingRoute.getDestination()
                            == route.getDestination();

            if (sameSource
                    && sameDestination) {

                throw new IllegalArgumentException(
                        "A route between these stations already exists."
                );
            }
        }

        routes.add(
                route
        );
    }

    /**
     * Finds a Route using its actual
     * source and destination Station objects.
     */
    public Route findRoute(
            Station source,
            Station destination) {

        for (Route route :
                routes) {

            if (route.getSource()
                    .equals(source)
                    && route.getDestination()
                    .equals(destination)) {

                return route;
            }
        }

        return null;
    }

    /**
     * Sorts Routes by Route ID
     * in ascending order.
     *
     * Example:
     * R001, R002, R003, R004...
     */
    public void sortRoutesById() {

        Comparator<Route> idComparator =
                Comparator.comparing(
                        Route::getRouteId,
                        String.CASE_INSENSITIVE_ORDER
                );

        routes.sort(
                idComparator
        );
    }

    /**
     * Displays all available Routes.
     */
    public void viewRoutes() {

        if (routes.isEmpty()) {

            System.out.println(
                    "No routes available."
            );

            return;
        }

        for (Route route :
                routes) {

            route.displayRoute();

            System.out.println(
                    "-------------------------"
            );
        }
    }
}