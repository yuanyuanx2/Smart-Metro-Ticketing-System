package app;

import exception.FileProcessingException;
import model.Station;
import repository.FileManager;
import repository.TXTFileManager;

import java.util.ArrayList;

public class FileHandlingTest {

    public static void main(String[] args) {

        FileManager fileManager = new TXTFileManager();

        String fileName = "src/main/resources/data/stations_test.txt";

        ArrayList<Station> stations = new ArrayList<>();

        stations.add(new Station(
                "S001",
                "KL Sentral",
                "Kuala Lumpur"
        ));

        stations.add(new Station(
                "S002",
                "Pasar Seni",
                "Kuala Lumpur"
        ));

        stations.add(new Station(
                "S003",
                "Masjid Jamek",
                "Kuala Lumpur"
        ));

        try {

            // Save Station objects
            fileManager.saveData(stations, fileName);

            System.out.println("Stations saved successfully.");

            // Load Station objects
            Object loadedData = fileManager.loadData(fileName);

            System.out.println("\nLoaded stations:");

            if (loadedData instanceof ArrayList<?>) {

                ArrayList<?> data = (ArrayList<?>) loadedData;

                for (Object item : data) {

                    if (item instanceof Station station) {

                        System.out.printf(
                                "%-4s | %-12s | %s%n",
                                station.getStationId(),
                                station.getName(),
                                station.getLocation()
                        );
                    }
                }
            }

        } catch (FileProcessingException e) {

            System.out.println(
                    "File processing error: " + e.getMessage()
            );
        }
    }
}