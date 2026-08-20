package repository;

import exception.FileProcessingException;

/**
 * Defines file saving and loading operations.
 */
public interface FileManager {

    void saveData(Object data, String fileName) throws FileProcessingException;

    Object loadData(String fileName) throws FileProcessingException;
}