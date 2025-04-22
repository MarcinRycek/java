package services;

import models.Rental;
import models.Vehicle;
import repositories.RentalRepository;
import repositories.VehicleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehicleJsonService implements VehicleService {
    private final VehicleRepository vehicleRepo;
    private final RentalRepository rentalRepo;

    public VehicleJsonService(VehicleRepository vehicleRepo, RentalRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepo.findAll();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicleRepo.findById(id);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepo.save(vehicle);
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        List<Vehicle> allVehicles = vehicleRepo.findAll();
        List<Rental> allRentals = rentalRepo.findAll();

        return allVehicles.stream()
                .filter(vehicle -> {
                    List<Rental> rentalsForVehicle = allRentals.stream()
                            .filter(r -> r.getVehicle() != null &&
                                    r.getVehicle().getId().equals(vehicle.getId()))
                            .sorted((r1, r2) -> r2.getRentDate().compareTo(r1.getRentDate())) // sortujemy malejąco po rentDate
                            .toList();

                    if (rentalsForVehicle.isEmpty()) {
                        return true; // nigdy nie był wypożyczony
                    }

                    Rental latestRental = rentalsForVehicle.get(0); // ostatnie wypożyczenie

                    return latestRental.getReturnDate() != null &&
                            !latestRental.getReturnDate().isBlank() &&
                            !latestRental.getReturnDate().equalsIgnoreCase("null");
                })
                .collect(Collectors.toList());
    }
    public List<Vehicle> findVehiclesRentedByUser(String userId) {
        return rentalRepo.findAll().stream()
                .filter(rental -> rental.getUser() != null &&
                        rental.getUser().getId().equals(userId))
                .map(Rental::getVehicle)
                .distinct()
                .collect(Collectors.toList());
    }





    @Override
    public boolean isAvailable(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isEmpty();
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        vehicleRepo.save(vehicle);
    }

    @Override
    public void showAll() {
        findAvailableVehicles().forEach(vehicle ->
                System.out.println(vehicle.getId() + " " +
                        vehicle.getCategory() + " " +
                        vehicle.getBrand() + " " +
                        vehicle.getModel()));
    }
    @Override
    public void showVehiclesCurrentlyRentedByUser(String userId) {
        List<Vehicle> rentedVehicles = rentalRepo.findAll().stream()
                .filter(r -> r.getUser() != null &&
                        r.getUser().getId().equals(userId) &&
                        (r.getReturnDate() == null || r.getReturnDate().isBlank() || r.getReturnDate().equalsIgnoreCase("null")))
                .map(Rental::getVehicle)
                .distinct()
                .toList();

        if (rentedVehicles.isEmpty()) {
            System.out.println("No vehicles currently rented.");
            System.out.println("==============================================");

        } else {
            System.out.println("Vehicles currently rented:");
            rentedVehicles.forEach(vehicle ->
                    System.out.println(vehicle.getId() + " " +
                            vehicle.getCategory() + " " +
                            vehicle.getBrand() + " " +
                            vehicle.getModel()));
            System.out.println("==============================================");
        }
    }


}
