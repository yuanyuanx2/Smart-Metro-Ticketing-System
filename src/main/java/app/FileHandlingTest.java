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

        try {

            // Test saving data
            fileManager.saveData(testData, fileName);
            System.out.println("Data saved successfully.");

            // Test loading data
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
    }
}