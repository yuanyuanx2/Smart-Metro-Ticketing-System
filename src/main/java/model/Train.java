package model;

/**
 * Represents a train in the Smart Metro Ticketing System.
 */
public class Train {

    private String trainId;
    private String trainName;
    private int capacity;

    /**
     * Creates a train with its ID, name and capacity.
     */
    public Train(String trainId, String trainName, int capacity) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.capacity = capacity;
    }

    /**
     * Displays the train information.
     */
    public void displayTrain() {
        System.out.println("Train ID   : " + trainId);
        System.out.println("Train Name : " + trainName);
        System.out.println("Capacity   : " + capacity);
    }
}