package services;

import models.Rental;
import models.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;
import repositories.impl.jdbc.RentalHibernateRepository;
import repositories.impl.jdbc.VehicleHibernateRepository;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateService implements VehicleService {
    private final VehicleHibernateRepository vehicleRepo;
    private final RentalHibernateRepository rentalRepo;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepo, RentalHibernateRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
//            List<Vehicle> vehicles= vehicleRepo.findAll();
//            vehicles.forEach(System.out::println);
           return vehicleRepo.findAll();
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findById(id);
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepo.setSession(session);
            Vehicle saved = vehicleRepo.save(vehicle);
            tx.commit();
            return saved;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            rentalRepo.setSession(session);
            return vehicleRepo.findAll().stream()
                    .filter(vehicle -> rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isEmpty())
                    .toList();
        }
    }

    @Override
    public boolean isAvailable(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isEmpty();
        }
    }

    public void addVehicle(Vehicle vehicle) {
        save(vehicle);
    }

    public void removeVehicle(String id) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepo.setSession(session);
            if (vehicleRepo.findById(id).isPresent()) {
                vehicleRepo.deleteById(id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void showAll() {
        findAvailableVehicles().forEach(vehicle ->
                System.out.println(vehicle.getId() + " " +
                        vehicle.getCategory() + " " +
                        vehicle.getBrand() + " " +
                        vehicle.getModel())
        );
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
            System.out.println("Użytkownik nie wypożycza obecnie żadnych pojazdów.");
        } else {
            System.out.println("Pojazdy aktualnie wypożyczone przez użytkownika:");
            rentedVehicles.forEach(vehicle ->
                    System.out.println(vehicle.getId() + " " +
                            vehicle.getCategory() + " " +
                            vehicle.getBrand() + " " +
                            vehicle.getModel()));
        }
    }
}

