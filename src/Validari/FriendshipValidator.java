package Validari;

import Domain.Friendship;
import Domain.User;
import Exceptions.RepoException;
import Exceptions.ValidationException;
import Repo.Repo;

public class FriendshipValidator implements Validator<Friendship>{
    private final Repo<User> userRepo;

    public FriendshipValidator(Repo<User> userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void valid(Friendship obj) {
        if(obj == null)
            throw new ValidationException("Entitatea friendship nu poate fii nula.");
        Integer id,id1,id2;
        id1 = obj.getUid1();
        id2 = obj.getUid2();
        StringBuilder err = new StringBuilder();
        if(id1.equals(id2))
            err.append("Id - urile nu pot sa fie identice.");
        try{userRepo.getById(id1);
        }catch(RepoException e) {
            err.append("Utiizatorul cu id-ul: ").append(id1).append(" nu exista.");
        }
        try{userRepo.getById(id2);
        }catch(RepoException e) {
            err.append("Utiizatorul cu id-ul: ").append(id2).append(" nu exista.");
        }
        if(!err.isEmpty())
            throw new ValidationException(err.toString());
    }
}
