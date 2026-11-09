package Repo;

import Domain.User;
import Validari.Validator;

import java.util.HashMap;

public class UserDbRepository implements Repo<User> {
    private final Validator<User> validator;
    private final String url;
    private final String user;
    private final String password;

    public UserDbRepository(Validator<User> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public HashMap<Integer, User> getAll() {
       throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getById(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User add(User obj) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User remove(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
