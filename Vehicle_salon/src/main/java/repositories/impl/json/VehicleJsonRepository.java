package repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import db.JsonFileStorage;
import models.Vehicle;
import models.VehicleJSON;
import repositories.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class VehicleJsonRepository implements VehicleRepository {

    private final JsonFileStorage<VehicleJSON> storage =
            new JsonFileStorage<>("vehicles.json", new TypeToken<List<VehicleJSON>>() {}.getType());

    private final List<Vehicle> vehicles;

    public VehicleJsonRepository() {
        this.vehicles = storage.load().stream()
                .map(VehicleJSON::getVehicle)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        } else {
            deleteById(vehicle.getId());
        }
        vehicles.add(vehicle);

        List<VehicleJSON> toSave = vehicles.stream()
                .map(v -> new VehicleJSON(UUID.randomUUID().toString(), v))
                .collect(Collectors.toList());
        storage.save(toSave);

        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(r -> r.getId().equals(id));
        List<VehicleJSON> toSave = vehicles.stream()
                .map(v -> new VehicleJSON(UUID.randomUUID().toString(), v))
                .collect(Collectors.toList());
        storage.save(toSave);
    }
}
