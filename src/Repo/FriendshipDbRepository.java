package Repo;

import Domain.Friendship;
import Domain.User;
import Validari.Validator;

import java.util.HashMap;

public class FriendshipDbRepository implements Repo<Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    public FriendshipDbRepository(Validator<Friendship> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public HashMap<Integer, Friendship> getAll() {
       throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Friendship getById(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Friendship add(Friendship obj) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Friendship remove(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
