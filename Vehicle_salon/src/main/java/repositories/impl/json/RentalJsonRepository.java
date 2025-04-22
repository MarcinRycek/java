package repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import db.JsonFileStorage;
import models.Rental;
import repositories.RentalRepository;

import java.util.*;

public class RentalJsonRepository implements RentalRepository {

    private final JsonFileStorage<Rental> storage =
            new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>(){}.getType());

    private final List<Rental> rentals;

    public RentalJsonRepository() {
        this.rentals = new ArrayList<>(storage.load());
    }

    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals);
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        } else {
            deleteById(rental.getId());
        }
        rentals.add(rental);
        storage.save(rentals);
        return rental;
    }

    @Override
    public void deleteById(String id) {
        rentals.removeIf(r -> r.getId().equals(id));
        storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(r -> r.getVehicle() != null &&
                        r.getVehicle().getId().equals(vehicleId) &&
                        (r.getReturnDate() == null ||
                                r.getReturnDate().isBlank() ||
                                r.getReturnDate().equalsIgnoreCase("null")))
                .findFirst();
    }




}
