package app;

import exception.FileProcessingException;
import model.Train;
import repository.FileManager;
import repository.TXTFileManager;

import java.util.ArrayList;

public class FileHandlingTest {

    public static void main(String[] args) {

        FileManager fileManager = new TXTFileManager();

        String fileName = "src/main/resources/data/trains_test.txt";

        ArrayList<Train> trains = new ArrayList<>();

        trains.add(new Train(
                "T001",
                "Metro Express",
                300
        ));

        trains.add(new Train(
                "T002",
                "City Line",
                250
        ));

        trains.add(new Train(
                "T003",
                "Rapid Metro",
                350
        ));

        try {

            // Save Train objects
            fileManager.saveData(trains, fileName);

            System.out.println("Trains saved successfully.");

            // Load Train objects
            Object loadedData = fileManager.loadData(fileName);

            System.out.println("\nLoaded trains:");

            if (loadedData instanceof ArrayList<?>) {

                ArrayList<?> data = (ArrayList<?>) loadedData;

                for (Object item : data) {

                    if (item instanceof Train train) {

                        System.out.printf(
                                "%-4s | %-13s | %d%n",
                                train.getTrainId(),
                                train.getTrainName(),
                                train.getCapacity()
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