package services;

import models.Rental;
import models.User;
import models.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;
import repositories.impl.jdbc.RentalHibernateRepository;
import repositories.impl.jdbc.UserHibernateRepository;
import repositories.impl.jdbc.VehicleHibernateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalHibernateService implements RentalService {
    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public RentalHibernateService(RentalHibernateRepository rentalRepo, VehicleHibernateRepository vehicleRepo,
                                  UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    @Override
    public boolean rent(String vehicleId, String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            rentalRepo.setSession(session);
            vehicleRepo.setSession(session);
            userRepo.setSession(session);
            if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
                throw new IllegalStateException("Vehicle is rented");
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
                    .build();
            rentalRepo.save(rental);
            tx.commit();

            return true;
        } catch (Exception e) {
//            if (tx != null && tx.isActive()) {
//                tx.rollback();
//            }
            //System.out.println("Cannot rent this vehicle!!!!");
        }
        return false;
    }

    @Override
    public boolean returnRental(String vehicleId, String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            rentalRepo.setSession(session);

            Optional<Rental> rentalOptional = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);
            if (rentalOptional.isPresent()) {
                Rental rental = rentalOptional.get();
                if (rental.getUser().getId().equals(userId)) {
                    rental.setReturnDate(LocalDateTime.now().toString());
                    rentalRepo.save(rental);
                    tx.commit();
                    return true;
                }
            }else {
                System.out.println("The vehicle isn't available.");
            }

            if (tx != null && tx.isActive()) tx.rollback();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public List<Rental> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()){
            rentalRepo.setSession(session);
            return rentalRepo.findAll();
        }
    }
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            rentalRepo.setSession(session);

            List<Vehicle> allVehicles = vehicleRepo.findAll();

            return allVehicles.stream()
                    .filter(vehicle -> rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isEmpty())
                    .toList();
        }
    }
}
