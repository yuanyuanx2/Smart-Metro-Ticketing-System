package app;

import exception.FileProcessingException;
import model.Route;
import model.Station;
import repository.FileManager;
import repository.TXTFileManager;

import java.util.ArrayList;

public class FileHandlingTest {

    public static void main(String[] args) {

        String stationFile =
                "src/main/resources/data/stations_test.txt";

        String routeFile =
                "src/main/resources/data/routes_test.txt";

        ArrayList<Station> stations = new ArrayList<>();

        Station s1 = new Station(
                "S001",
                "KL Sentral",
                "Kuala Lumpur"
        );

        Station s2 = new Station(
                "S002",
                "Pasar Seni",
                "Kuala Lumpur"
        );

        Station s3 = new Station(
                "S003",
                "Masjid Jamek",
                "Kuala Lumpur"
        );

        stations.add(s1);
        stations.add(s2);
        stations.add(s3);

        ArrayList<Route> routes = new ArrayList<>();

        routes.add(new Route(
                "R001",
                s1,
                s2,
                2.5
        ));

        routes.add(new Route(
                "R002",
                s2,
                s3,
                1.8
        ));

        try {

            // Save using one file manager
            FileManager saveManager =
                    new TXTFileManager();

            saveManager.saveData(
                    stations,
                    stationFile
            );

            saveManager.saveData(
                    routes,
                    routeFile
            );

            System.out.println(
                    "Stations and routes saved successfully."
            );

            /*
             * Use a fresh TXTFileManager to simulate
             * starting the program again.
             */
            FileManager loadManager =
                    new TXTFileManager();

            // Stations must be loaded before routes
            Object loadedStationData =
                    loadManager.loadData(stationFile);

            Object loadedRouteData =
                    loadManager.loadData(routeFile);

            ArrayList<Station> loadedStations =
                    new ArrayList<>();

            ArrayList<Route> loadedRoutes =
                    new ArrayList<>();

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

            System.out.println("\nLoaded routes:");

            for (Route route : loadedRoutes) {

                System.out.printf(
                        "%-4s | %-12s -> %-12s | %.1f km%n",
                        route.getRouteId(),
                        route.getSource().getName(),
                        route.getDestination().getName(),
                        route.calculateDistance()
                );
            }

            /*
             * Check that loaded routes reuse the exact
             * loaded Station objects.
             */
            Route firstRoute = loadedRoutes.get(0);

            boolean sameSourceObject =
                    firstRoute.getSource()
                            == loadedStations.get(0);

            boolean sameDestinationObject =
                    firstRoute.getDestination()
                            == loadedStations.get(1);

            System.out.println(
                    "\nStation reference check:"
            );

            System.out.println(
                    "Source uses loaded Station object      : "
                            + sameSourceObject
            );

            System.out.println(
                    "Destination uses loaded Station object : "
                            + sameDestinationObject
            );

        } catch (FileProcessingException e) {

            System.out.println(
                    "File processing error: "
                            + e.getMessage()
            );
        }
    }
}