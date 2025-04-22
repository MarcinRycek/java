package services;

import models.User;
import org.mindrot.jbcrypt.BCrypt;
import repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJsonService implements AuthService {
    private final UserRepository userRepo;

    public UserJsonService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register(String login, String rawPassword, String role) {
        if (userRepo.findByLogin(login).isPresent()) {
            throw new IllegalStateException("Użytkownik już istnieje");
        }
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setLogin(login);
        user.setPassword(hashed);
        user.setRole(role);

        userRepo.save(user);
        return true;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        return userRepo.findByLogin(login)
                .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()));
    }

    @Override
    public void getUsers() {
        List<User> users=userRepo.findAll();
        users.forEach(user -> System.out.println(user.getLogin()));
    }
}
