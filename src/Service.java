import java.util.Collection;

public class Service {
    Repo<User> userRepo;
    Repo<Friendship> friendshipRepo;

    public Service(Repo<User> userRepo, Repo<Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }
    public User AddUser(String username){
        User aux = new User(username);
         return userRepo.add(aux);
    }
    public Friendship AddFriendship(int uid1, int uid2){
        return friendshipRepo.add(new Friendship(uid1,uid2));
    }
    public User RemoveUser(Integer id){
        return userRepo.remove(id);
    }
    public Friendship RemoveFriendship(Integer id){
        return friendshipRepo.remove(id);
    }
    public Collection<User> get_all_users(){
        return userRepo.getAll().values();
    }
    public Collection<Friendship> get_all_friendships(){
        return friendshipRepo.getAll().values();
    }
}
