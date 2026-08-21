package app;

import exception.FileProcessingException;
import model.Admin;
import model.Passenger;
import model.User;
import repository.FileManager;
import repository.TXTFileManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FileHandlingTest {

    public static void main(String[] args) {

        String userFile =
                "src/main/resources/data/users_test.txt";

        HashMap<String, User> users =
                new HashMap<>();

        Passenger passenger =
                new Passenger(
                        "P001",
                        "Ali",
                        "ali@email.com",
                        "pass123",
                        75.50
                );

        Admin admin =
                new Admin(
                        "A001",
                        "Metro Admin",
                        "admin@email.com",
                        "admin123"
                );

        users.put(passenger.getUserId(), passenger);
        users.put(admin.getUserId(), admin);

        try {

            // Save the lecturer-required HashMap<String, User>
            FileManager saveManager =
                    new TXTFileManager();

            saveManager.saveData(
                    users,
                    userFile
            );

            System.out.println(
                    "Users saved successfully."
            );

            /*
             * Fresh manager simulates restarting
             * the application.
             */
            FileManager loadManager =
                    new TXTFileManager();

            Object loadedData =
                    loadManager.loadData(userFile);

            HashMap<String, User> loadedUsers =
                    new HashMap<>();

            if (loadedData instanceof ArrayList<?> data) {

                for (Object item : data) {

                    if (item instanceof User user) {

                        loadedUsers.put(
                                user.getUserId(),
                                user
                        );
                    }
                }
            }

            Passenger loadedPassenger =
                    (Passenger) loadedUsers.get("P001");

            Admin loadedAdmin =
                    (Admin) loadedUsers.get("A001");

            System.out.println("\nLoaded passenger:");

            System.out.printf(
                    "%s | %s | %s | %s | RM %.2f%n",
                    loadedPassenger.getUserId(),
                    loadedPassenger.getName(),
                    loadedPassenger.getEmail(),
                    loadedPassenger.getRole(),
                    loadedPassenger.getBalance()
            );

            System.out.println("\nLoaded admin:");

            System.out.printf(
                    "%s | %s | %s | %s%n",
                    loadedAdmin.getUserId(),
                    loadedAdmin.getName(),
                    loadedAdmin.getEmail(),
                    loadedAdmin.getRole()
            );

            System.out.println(
                    "\nLogin restoration check:"
            );

            System.out.println(
                    "Passenger login works : "
                            + loadedPassenger.login(
                            "ali@email.com",
                            "pass123"
                    )
            );

            System.out.println(
                    "Admin login works     : "
                            + loadedAdmin.login(
                            "admin@email.com",
                            "admin123"
                    )
            );

            System.out.println(
                    "\nBalance restoration check:"
            );

            System.out.printf(
                    "Passenger balance: RM %.2f%n",
                    loadedPassenger.getBalance()
            );

        } catch (FileProcessingException e) {

            System.out.println(
                    "File processing error: "
                            + e.getMessage()
            );
        }
    }
}