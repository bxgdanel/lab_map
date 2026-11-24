package Service;

import Domain.Friendship;
import Domain.Tuple;
import Domain.User;
import Exceptions.RepoException;
import Repo.Repo;
import Repo.CriteriiUtilizator;
import paging.Page;
import Repo.PagingRepository;
import paging.PageRequest;
import Repo.FriendshipPagingRepo;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Repo.CriteriiUtilizator.hasFriends;
import static Repo.CriteriiUtilizator.hasLastName;


public class Service {
    Repo<Long,User> userRepo;
    Repo<Tuple<Long,Long>,Friendship> friendshipRepo;

    public Service(Repo<Long,User> userRepo, Repo<Tuple<Long,Long>,Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * Gets users using paging
     * @param pageNumber - number of the page to get
     * @param pageSize - page size
     * @return - a page containing users
     */
    public Page<User> getUsersPage(int pageNumber, int pageSize){
        if(userRepo instanceof PagingRepository<Long,User> paging)
            return paging.findAllOnPage(new PageRequest(pageNumber,pageSize));
        throw new UnsupportedOperationException("Paginarea este disponibila doar pentru repository.type = DATABASE");
    }

    /**
     * Gets friends of user using paging
     * @param uid - id of user to get friends of
     * @param pageNumber - number of the page to get
     * @param pageSize - page size
     * @return - a page containing users
     */
    public Page<User> getFriendsPage(Long uid,int pageNumber,int pageSize){
        if(friendshipRepo instanceof FriendshipPagingRepo paging)
            return paging.findFriendsOnPage(uid,new PageRequest(pageNumber,pageSize));
        throw new UnsupportedOperationException("Paginarea este disponibila doar pentru repository.type = DATABASE");
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
    public Friendship AddFriendship(Long uid1,Long uid2){
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
    public User RemoveUser(Long id){
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
    public List<User> getFriendsOfUser(Long uid){
        userRepo.getById(uid);
        return friendshipRepo.getAll().values().stream().filter(f-> f.getUid1() == uid || f.getUid2()==uid)
                .map(f-> f.getUid1() == uid ? f.getUid2() : f.getUid1())
                .map(userRepo::getById)
                .collect(Collectors.toList());
    }
    /**
     * @return - adjacency list of the graph
     */
    private Map<Long, List<Long>> buildGraph() {
        Map<Long, List<Long>> adj = userRepo.getAll().values().stream().collect(
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
    private List<Long> dfsComponent(Long node, Map<Long, List<Long>> adj, Set<Long> visited) {
        visited.add(node);
        List<Long> component = adj.get(node).stream()
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
    private List<List<Long>> getAllCommunities(Map<Long, List<Long>> adj) {
        Set<Long> visited = new HashSet<>();
       return  adj.keySet().stream().filter(userId -> !visited.contains(userId))
                .map(userId -> dfsComponent(userId,adj,visited))
                .collect(Collectors.toList());
    }

    /**
     * @return - number of cimmunities
     */
    public int getNumberOfCommunities() {
        Map<Long, List<Long>> adj = buildGraph();
        return getAllCommunities(adj).size();
    }

    /**
     * @param current - the current node
     * @param adj - adjacency list
     * @param visitedInPath - set of visited nodes in the current path
     * @return - the longest path starting from the current node
     */
    private int longestPathFrom(Long current, Map<Long, List<Long>> adj, Set<Long> visitedInPath) {
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
        Map<Long, List<Long>> adj = buildGraph();
        List<List<Long>> communities = getAllCommunities(adj);

        List<Long> bestCommunityIds = communities.stream()
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
    public Friendship RemoveFriendship(Long uid1, Long uid2) {
        Tuple<Long,Long> id = new Tuple<>(Math.min(uid1,uid2), Math.max(uid1,uid2));
        Friendship f = friendshipRepo.remove(id);
        if (f == null) throw new RepoException("Prietenia dintre acesti utilizatori nu exista!");
        return f;
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
     * @return a collection of all the users that have friends and whose name contains the given string
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
    public Optional<User> findUser(Long id){
        return Optional.ofNullable(userRepo.getAll().get(id));
    }
}
