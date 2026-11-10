package Service;

import Domain.Friendship;
import Domain.User;
import Exceptions.RepoException;
import Repo.Repo;
import Repo.CriteriiUtilizator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Repo.CriteriiUtilizator.hasFriends;
import static Repo.CriteriiUtilizator.hasLastName;


public class Service {
    Repo<User> userRepo;
    Repo<Friendship> friendshipRepo;

    public Service(Repo<User> userRepo, Repo<Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * @param username - the username of the user to be added
     * @return - the added user with the associated id
     */
    public User AddUser(String username){
        User aux = new User(username);
         return userRepo.add(aux);
    }

    /**
     * Adauga o prietenie noua
     * @param uid1
     * @param uid2
     * @return
     */
    public Friendship AddFriendship(int uid1, int uid2){
        userRepo.getById(uid1);
        userRepo.getById(uid2);
        Friendship aux = new Friendship(uid1,uid2);
        if(friendshipRepo.getAll().containsValue(aux))
            throw new RepoException("Prietenia exista deja!");
        return friendshipRepo.add(new Friendship(LocalDateTime.now(),uid1,uid2));
    }

    /**
     * @param id - id of the user to be deleted
     * @return - the deleted user
     */
    public User RemoveUser(Integer id){
        List<Friendship> toDelete = friendshipRepo.getAll().values().stream().filter(friendship ->
                friendship.getUid1() == id ||
                friendship.getUid2() == id).collect(Collectors.toList());
        toDelete.forEach(f->friendshipRepo.remove(f.getId()));
        return userRepo.remove(id);
    }

    /**
     *
     * @param uid - id of user whose friends to search
     * @return - a list with all the friends
     */
    public List<User> getFriendsOfUser(int uid){
        userRepo.getById(uid);
        return friendshipRepo.getAll().values().stream().filter(f-> f.getUid1() == uid || f.getUid2()==uid)
                .map(f-> f.getUid1() == uid ? f.getUid2() : f.getUid1())
                .map(userRepo::getById)
                .collect(Collectors.toList());
    }
    /**
     * @return - adjacency list of the graph
     */
    private Map<Integer, List<Integer>> buildGraph() {
        Map<Integer, List<Integer>> adj = userRepo.getAll().values().stream().collect(
                Collectors.toMap(User::getId,u->new ArrayList<>()));
        friendshipRepo.getAll().values().stream()
                .filter(f-> adj.containsKey(f.getUid1()) && adj.containsKey(f.getUid2()))
                .forEach(f->{
                    adj.get(f.getUid1()).add(f.getUid2());
                    adj.get(f.getUid2()).add(f.getUid1());
                });

        return adj;
    }

    /**
     * @param node - starting node
     * @param adj - adjacency list
     * @param visited - set of visited nodes
     * @return - list of the nodes in the same connex component
     */
    private List<Integer> dfsComponent(int node, Map<Integer, List<Integer>> adj, Set<Integer> visited) {
        visited.add(node);
        List<Integer> component = adj.get(node).stream()
                .filter(n->!visited.contains(n))
                .flatMap(n->dfsComponent(n,adj,visited).stream())
                .collect(Collectors.toList());
        return Stream.concat(Stream.of(node),component.stream())
                .collect(Collectors.toList());
    }

    /**
     * @param adj - adjacency list
     * @return - a list of all the conex components
     */
    private List<List<Integer>> getAllCommunities(Map<Integer, List<Integer>> adj) {
        Set<Integer> visited = new HashSet<>();
       return  adj.keySet().stream().filter(userId -> !visited.contains(userId))
                .map(userId -> dfsComponent(userId,adj,visited))
                .collect(Collectors.toList());
    }

    /**
     * @return - number of cimmunities
     */
    public int getNumberOfCommunities() {
        Map<Integer, List<Integer>> adj = buildGraph();
        return getAllCommunities(adj).size();
    }

    /**
     * @param current - the current node
     * @param adj - adjacency list
     * @param visitedInPath - set of visited nodes in the current path
     * @return - the longest path starting from the current node
     */
    private int longestPathFrom(int current, Map<Integer, List<Integer>> adj, Set<Integer> visitedInPath) {
        visitedInPath.add(current);
        int maxDist = adj.getOrDefault(current,Collections.emptyList()).stream()
                .filter(v->!visitedInPath.contains(v))
                .mapToInt(v->1+longestPathFrom(v,adj,visitedInPath))
                .max()
                .orElse(0);
        visitedInPath.remove(current);
        return maxDist;
    }

    /**
     * @return - the most sociable community
     */
    public List<User> getMostSociableCommunity() {
        Map<Integer, List<Integer>> adj = buildGraph();
        List<List<Integer>> communities = getAllCommunities(adj);

        List<Integer> bestCommunityIds = communities.stream()
                .max(Comparator.comparingInt(lst ->{
                   return lst.stream().map(el -> longestPathFrom(el,adj,new HashSet<>()))
                            .max(Comparator.naturalOrder()).orElse(0);
                }))
                .orElse(List.of());
        return bestCommunityIds.stream().map(userRepo::getById).collect(Collectors.toList());
    }

    /**
     * @param uid1 - the id of the first friend in the friendship
     * @param uid2 - the id of the second friend in the friendship
     * @return the friendship that was deleted from the repo
     */
    public Friendship RemoveFriendship(int uid1, int uid2) {
        return friendshipRepo.getAll().values().stream().filter(f -> {return f.getUid1() == uid1 && f.getUid2() == uid2;})
                     .findFirst()
             .map(f->friendshipRepo.remove(f.getId()))
            .orElseThrow(() -> new RepoException("Prietenia dintre acesti utilizatori nu exista!"));

}
    /**
     * @return all the users
     */
    public Collection<User> get_all_users(){
        return userRepo.getAll().values();
    }

    /**
     *
     * @return Collection<Friendship> - all friendships
     */
    public Collection<Friendship> get_all_friendships(){
        return friendshipRepo.getAll().values();
    }

    /**
     *
     * @param name - to filter by
     * @return a collection of all the users whose name contains the given string
     */
    public Collection<User> get_by_name(String name){
        return userRepo.findAll(hasLastName(name).and(hasFriends(friendshipRepo)));
    }

    /**
     *
     * @return a list of all users with a valid username
     */
    public List<User> getValidUsers(){
        return userRepo.findAll(CriteriiUtilizator::hasValidUsername);
    }

    /**
     *
     * @param id - to search by
     * @return Optional<User> which has the id
     */
    public Optional<User> findUser(int id){
        return Optional.ofNullable(userRepo.getAll().get(id));
    }
}
