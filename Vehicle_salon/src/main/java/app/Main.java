package app;

import db.JdbcConnectionManager;
import repositories.RentalRepository;
import repositories.UserRepository;
import repositories.VehicleRepository;
import repositories.impl.jdbc.*;
import repositories.impl.json.RentalJsonRepository;
import repositories.impl.json.UserJsonRepository;
import repositories.impl.json.VehicleJsonRepository;
import services.*;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose storage type(1-json/2-database)");

        String storageType = "";
        String choose = scanner.nextLine();

        if (choose.equals("1")) {
            storageType = "json";
        } else if (choose.equals("2")) {
            storageType = "jdbc";
        }


        AuthService userService;
        VehicleService vehicleService;
        RentalService rentalService;

        switch (storageType) {
            case "jdbc"-> {
                var userRepo = new UserHibernateRepository();
                var vehicleRepo = new VehicleHibernateRepository();
                var rentalRepo = new RentalHibernateRepository();

                userService = new UserHibernateService(userRepo);
                vehicleService = new VehicleHibernateService(vehicleRepo, rentalRepo);
                rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);

            }case "json" -> {
                var userRepo = new UserJsonRepository();
                var vehicleRepo = new VehicleJsonRepository();
                var rentalRepo = new RentalJsonRepository();

                userService = new UserJsonService(userRepo);
                vehicleService = new VehicleJsonService(vehicleRepo, rentalRepo);
                rentalService = new RentalJsonService(rentalRepo, vehicleRepo, userRepo);
            }

            default -> throw new IllegalArgumentException("Unknown storage type: " + storageType);
        }
        App app = new App(userService, vehicleService, rentalService);
        app.run();



    }
}