package services;

import models.Rental;
import models.User;
import models.Vehicle;
import repositories.RentalRepository;
import repositories.UserRepository;
import repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RentalJsonService implements RentalService {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;

    public RentalJsonService(RentalRepository rentalRepo, VehicleRepository vehicleRepo, UserRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
    public List<Vehicle> findAvailableVehicles() {
        List<Rental> allRentals = rentalRepo.findAll();

        return vehicleRepo.findAll().stream()
                .filter(vehicle -> {
                    // znajdź wszystkie wypożyczenia tego pojazdu
                    List<Rental> vehicleRentals = allRentals.stream()
                            .filter(r -> r.getVehicle() != null && r.getVehicle().getId().equals(vehicle.getId()))
                            .toList();

                    // jeśli nie było wypożyczeń, pojazd jest dostępny
                    if (vehicleRentals.isEmpty()) return true;

                    // sprawdź ostatnie wypożyczenie (czy ma returnDate)
                    Rental lastRental = vehicleRentals.get(vehicleRentals.size() - 1);
                    return lastRental.getReturnDate() != null && !lastRental.getReturnDate().isBlank();
                })
                .toList();
    }


    @Override
    public boolean rent(String vehicleId, String userId) {
        if (isVehicleRented(vehicleId)) {
            System.out.println("Vehicle already rented.");
            return false;
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDate(LocalDateTime.now().toString())
                .returnDate(null)
                .build();

        rentalRepo.save(rental);
        return true;
    }

    @Override
    public boolean returnRental(String vehicleId, String userId) {
        Optional<Rental> rentalOptional = rentalRepo.findAll().stream()
            .filter(r -> r.getVehicle() != null &&
                    r.getUser() != null &&
                    r.getVehicle().getId().equals(vehicleId) &&
                    r.getUser().getId().equals(userId) &&
                    (Objects.equals(r.getReturnDate(), null) || r.getReturnDate().isBlank()))
            .findFirst();


        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();
            rental.setReturnDate(LocalDateTime.now().toString());
            rentalRepo.save(rental);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepo.findAll();
    }


}
