package services;

import models.Rental;
import models.Vehicle;

import java.util.List;

public interface RentalService {
    boolean isVehicleRented(String vehicleId);
    boolean rent(String vehicleId, String userId);
    boolean returnRental(String vehicleId, String userId);
    List<Rental> findAll();
}