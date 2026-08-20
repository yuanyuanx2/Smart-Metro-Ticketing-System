package repository;

import exception.FileProcessingException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles saving and loading data using TXT files.
 */
public class TXTFileManager implements FileManager {

    @Override
    public void saveData(Object data, String fileName)
            throws FileProcessingException {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(fileName))) {

            if (data instanceof Iterable<?> collection) {

                for (Object item : collection) {
                    writer.write(String.valueOf(item));
                    writer.newLine();
                }

            } else {
                writer.write(String.valueOf(data));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new FileProcessingException(
                    "Unable to save data to file: " + fileName
            );
        }
    }

    @Override
    public Object loadData(String fileName)
            throws FileProcessingException {

        ArrayList<String> data = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {
                data.add(line);
            }

        } catch (IOException e) {
            throw new FileProcessingException(
                    "Unable to load data from file: " + fileName
            );
        }

        return data;
    }
}