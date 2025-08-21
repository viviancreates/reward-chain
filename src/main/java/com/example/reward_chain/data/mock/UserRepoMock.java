package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Profile("mock")
@SuppressWarnings("unused")
public class UserRepoMock implements UserRepo {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public UserRepoMock() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        addUser(new User("Vivian", "Davila", "vivian@example.com", "secret123"));
        addUser(new User("Alice", "Smith", "alice@example.com", "pass1"));
        addUser(new User("Bob", "Johnson", "bob@example.com", "pass2"));
    }

    @Override
    public User getUserById(int id) throws RecordNotFoundException {
        User u = users.get(id);
        if (u == null) throw new RecordNotFoundException("User not found");
        return u;
    }

    @Override
    public User getUserByEmail(String email) throws RecordNotFoundException {
        return users.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException());
    }

    @Override
    public List<User> getAllUsers() { return new ArrayList<>(users.values()); }

    @Override
    public User addUser(User user) {
        user.setUserId(idCounter.getAndIncrement());
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public void updateUser(User user) throws InternalErrorException {
        if (!users.containsKey(user.getUserId())) {
            throw new InternalErrorException(new RecordNotFoundException());
        }
        users.put(user.getUserId(), user);
    }

    @Override
    public User deleteUser(int id) { return users.remove(id); }
}
