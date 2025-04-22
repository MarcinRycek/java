package app;

import models.User;
import models.Vehicle;
import services.AuthService;
import services.RentalService;
import services.VehicleService;

import java.util.*;

public class App {

    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final Scanner scanner = new Scanner(System.in);

    public App(AuthService authService, VehicleService vehicleService, RentalService rentalService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
    }

    public void run() {
        Scanner scanner1= new Scanner(System.in);
        int input=0;
        int selection=0;

        while (input!=3) {
            System.out.println("1. Log in");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            input = (scanner1.nextInt());
            scanner1.nextLine();
            if(input==1){
                System.out.println("Enter login");
                String login=scanner1.nextLine();
                System.out.println("Enter password");
                String password= scanner1.nextLine();
                if(authService.login(login, password).isPresent()){
                    User user=authService.login(login, password).get();
                    String userID= user.getId();
                    System.out.println("Welcome "+login+".");
                    if (user.getRole().equals("USER")) {
                    while (selection!=4) {

                            System.out.println("1.Rent vehicle");
                            System.out.println("2.Return vehicle");
                            System.out.println("3.See available vehicles");
                            System.out.println("4.Log out");
                            selection = scanner1.nextInt();
                            scanner1.nextLine();
                            if (selection == 1) {
                                System.out.println("Select id of vehicle:");
                                String id = scanner1.nextLine();
                                if (rentalService.rent(id, userID)) {
                                    System.out.println("Successfully rented.");
                                } else {
                                    System.out.println("Couldn't rent.");
                                }
                            } else if (selection == 2) {
                                System.out.println("Select id of vehicle:");
                                String id = scanner1.nextLine();
                                if (rentalService.returnRental(id, userID)){
                                    System.out.println("Successfully returned vehicle.");
                                }else {
                                    System.out.println("Couldn't return vehicle.");
                                }
                            } else if (selection == 3) {

                                vehicleService.showAll();
                                vehicleService.showVehiclesCurrentlyRentedByUser(userID);

                            }
                        }
                    }else if (user.getRole().equals("ADMIN")) {
                        while (selection!=6) {

                            System.out.println("1. Rent vehicle");
                            System.out.println("2. Return Vehicle");
                            System.out.println("3. Show available vehicles");
                            System.out.println("4. Add vehicle");
                            System.out.println("5. Show users");//TODO: dodaj usuwanie vehiclow i w database wyswietlanie przez uzytkownika przez jego i laczenie z baza na poczatkiu
                            System.out.println("6. Log out");
                            selection = scanner1.nextInt();
                            scanner1.nextLine();
                            if (selection == 1) {
                                System.out.println("Select id of vehicle:");
                                String id = scanner1.nextLine();
                                if (rentalService.rent(id, userID)) {
                                    System.out.println("Successfully rented.");
                                } else {
                                    System.out.println("Couldn't rent.");
                                }
                            } else if (selection == 2) {
                                System.out.println("Select id of vehicle:");
                                String id = scanner1.nextLine();
                                if (rentalService.returnRental(id, userID)){
                                    System.out.println("Successfully returned vehicle.");
                                }else {
                                    System.out.println("Couldn't return vehicle.");
                                }
                            } else if (selection == 3) {
                                vehicleService.showAll();
                                vehicleService.showVehiclesCurrentlyRentedByUser(userID);
                            } else if (selection==4) {
                                System.out.println("Enter vehicle brand:");
                                String brand= scanner1.nextLine();
                                System.out.println("Enter vehicle model:");
                                String model= scanner1.nextLine();
                                System.out.println("Enter the price");
                                double price=scanner1.nextInt();
                                scanner1.nextLine();
                                System.out.println("Enter vehicle year of production:");
                                int year= Integer.parseInt(scanner1.nextLine());
                                System.out.println("Enter vehicle plate number:");
                                String plate=scanner1.nextLine();
                                System.out.println("Enter vehicle category:");
                                String category= scanner1.nextLine();
                                System.out.println("Do you want to add attribute?(yes/no)");
                                String isAtt=scanner1.nextLine();
                                Map<String, Object> atributes=new HashMap<>();
                                while (isAtt.equals("yes")){
                                    System.out.println("Enter key");
                                    String key= scanner1.nextLine();
                                    System.out.println("Enter value");
                                    String value= scanner1.nextLine();
                                    atributes.put(key, value);
                                    System.out.println("Do you want to add another one?(yes/no)");
                                    isAtt=scanner1.nextLine();
                                }
                                List<Vehicle> vehicles=vehicleService.findAll();
                                String id= String.valueOf(vehicles.stream().mapToInt(vehicle->{
                                            return Integer.parseInt((vehicle.getId()));
                                        })
                                        .max().orElse(0)+1);
                                Vehicle vehicle=new Vehicle(id, price, category, brand, model, year, plate, atributes);
                                vehicleService.addVehicle(vehicle);


                            } else if (selection==5) {
                                authService.getUsers();
                            }
                        }
                    }
                }else {
                    System.out.println("Wrong credentials!!");
                }
            } else if (input==2) {
                System.out.println("Enter login");
                String login=scanner1.nextLine();
                System.out.println("Enter password");
                String password= scanner1.nextLine();
                System.out.println("Are you admin or user(admin/user)");
                String role= scanner1.nextLine();
                if (authService.register(login, password, role.toUpperCase())){
                    System.out.println("Registered new "+role+".");
                }else {
                    System.out.println("Wrong input!!");
                }
            }
        }
    }
}
