package repository;

import exception.FileProcessingException;
import model.Station;

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
                    writer.write(convertToText(item));
                    writer.newLine();
                }

            } else {
                writer.write(convertToText(data));
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

        ArrayList<Object> data = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {
                data.add(convertFromText(line));
            }

        } catch (IOException e) {
            throw new FileProcessingException(
                    "Unable to load data from file: " + fileName
            );
        }

        return data;
    }

    /**
     * Converts supported Java objects into TXT records.
     */
    private String convertToText(Object item)
            throws FileProcessingException {

        if (item instanceof Station station) {

            return "STATION|"
                    + station.getStationId() + "|"
                    + station.getName() + "|"
                    + station.getLocation();
        }

        if (item instanceof String text) {
            return text;
        }

        throw new FileProcessingException(
                "Unsupported data type for TXT saving."
        );
    }

    /**
     * Converts TXT records back into Java objects.
     */
    private Object convertFromText(String line)
            throws FileProcessingException {

        if (line.startsWith("STATION|")) {

            String[] parts = line.split("\\|", -1);

            if (parts.length != 4) {
                throw new FileProcessingException(
                        "Invalid station data: " + line
                );
            }

            return new Station(
                    parts[1],
                    parts[2],
                    parts[3]
            );
        }

        return line;
    }
}