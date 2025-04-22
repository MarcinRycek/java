package services;

import models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import repositories.impl.jdbc.UserHibernateRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserHibernateService implements AuthService {
    private final UserHibernateRepository userRepo;

    public UserHibernateService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            return userRepo.findAll();
        }
    }
    public void getUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            List<User> users = userRepo.findAll();
            users.forEach(user -> System.out.println(user.getLogin()));
        }
    }


    public Optional<User> getUserById(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            return userRepo.findById(userId);
        }
    }

    public User saveUser(User user) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);
            User savedUser = userRepo.save(user);
            tx.commit();
            return savedUser;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public void deleteUserById(String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);
            userRepo.deleteById(userId);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
    @Override
    public boolean register(String login, String password, String role) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);

            if (userRepo.findByLogin(login).isPresent()) {
                throw new IllegalStateException("User with this email already exists");
            }

            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(role);

            User savedUser = userRepo.save(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            return userRepo.findByLogin(login)
                    .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()));
        }
    }
}

