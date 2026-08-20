package app;

import exception.FileProcessingException;
import repository.FileManager;
import repository.TXTFileManager;

import java.util.ArrayList;

public class FileHandlingTest {

    public static void main(String[] args) {

        FileManager fileManager = new TXTFileManager();

        String fileName = "src/main/resources/data/file_test.txt";

        ArrayList<String> testData = new ArrayList<>();

        testData.add("KL Sentral");
        testData.add("Pasar Seni");
        testData.add("Masjid Jamek");

        // Test 1: Normal save and load
        try {

            fileManager.saveData(testData, fileName);
            System.out.println("Data saved successfully.");

            Object loadedData = fileManager.loadData(fileName);

            System.out.println("\nLoaded data:");

            if (loadedData instanceof ArrayList<?>) {

                ArrayList<?> data = (ArrayList<?>) loadedData;

                for (Object item : data) {
                    System.out.println(item);
                }
            }

        } catch (FileProcessingException e) {

            System.out.println("File processing error: " + e.getMessage());
        }

        // Test 2: Load a file that does not exist
        System.out.println("\nTesting missing file:");

        try {

            fileManager.loadData(
                    "src/main/resources/data/does_not_exist.txt"
            );

        } catch (FileProcessingException e) {

            System.out.println("Exception handled successfully.");
            System.out.println(e.getMessage());
        }
    }
}