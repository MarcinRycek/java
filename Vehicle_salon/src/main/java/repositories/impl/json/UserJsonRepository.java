package repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import db.JsonFileStorage;
import models.Rental;
import models.User;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJsonRepository implements UserRepository {
    private final JsonFileStorage<User> storage =
            new JsonFileStorage<>("users.json", new TypeToken<List<User>>(){}.getType());
    private List<User> users;
    public UserJsonRepository(){
        this.users = new ArrayList<>(storage.load());
    }
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream().filter(r -> r.getId().equals(id)).findFirst();    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(r -> r.getLogin().equals(login))
                .findFirst();    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        } else {
            deleteById(user.getId());
        }
        users.add(user);
        storage.save(users);
        return user;    }

    @Override
    public void deleteById(String id) {
        users.removeIf(r -> r.getId().equals(id));
        storage.save(users);
    }
}
