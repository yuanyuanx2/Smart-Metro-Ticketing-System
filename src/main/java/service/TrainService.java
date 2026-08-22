package service;

import model.Train;

import java.util.ArrayList;

/**
 * Handles train management operations.
 */
public class TrainService {

    private ArrayList<Train> trains;

    /**
     * Creates an empty train list.
     */
    public TrainService() {
        trains = new ArrayList<>();
    }

    /**
     * Adds a train to the train list.
     *
     * Train IDs must be unique.
     */
    public void addTrain(Train train) {

        if (train == null) {
            throw new IllegalArgumentException(
                    "Train cannot be null."
            );
        }

        for (Train existingTrain : trains) {

            if (existingTrain.getTrainId()
                    .equalsIgnoreCase(train.getTrainId())) {

                throw new IllegalArgumentException(
                        "Train ID already exists: "
                                + train.getTrainId()
                );
            }
        }

        trains.add(train);
    }

    /**
     * Displays all trains.
     */
    public void viewTrains() {

        if (trains.isEmpty()) {

            System.out.println(
                    "No trains available."
            );

            return;
        }

        for (Train train : trains) {

            train.displayTrain();

            System.out.println(
                    "-------------------------"
            );
        }
    }
}