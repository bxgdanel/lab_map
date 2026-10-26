package Service;

import Domain.Friendship;
import Domain.User;
import Exceptions.RepoException;
import Repo.Repo;

import java.util.*;

public class Service {
    Repo<User> userRepo;
    Repo<Friendship> friendshipRepo;

    public Service(Repo<User> userRepo, Repo<Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * Adauga un utilizator nou
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
        return friendshipRepo.add(new Friendship(uid1,uid2));
    }

    /**
     * Sterge un utilizator si prieteniile asociate lui
     */
    public User RemoveUser(Integer id){
        ArrayList<Integer> idDeSters = new ArrayList<>();
        for(var f:friendshipRepo.getAll().values())
            if(f.getUid1() == id || f.getUid2() == id)
                idDeSters.add(f.getId());
        for(var i:idDeSters)
            friendshipRepo.remove(i);
        return userRepo.remove(id);
    }
    /**
     * Construieste lista de adiacenta a grafului retelei.
     */
    private Map<Integer, List<Integer>> buildGraph() {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (User u : userRepo.getAll().values()) {
            adj.put(u.getId(), new ArrayList<>());
        }
        for (Friendship f : friendshipRepo.getAll().values()) {
            if (adj.containsKey(f.getUid1()) && adj.containsKey(f.getUid2())) {
                adj.get(f.getUid1()).add(f.getUid2());
                adj.get(f.getUid2()).add(f.getUid1());
            }
        }
        return adj;
    }

    /**
     * DFS pentru determinarea tuturor nodurilor dintr-o componenta conexa.
     */
    private void dfsComponent(int node, Map<Integer, List<Integer>> adj, Set<Integer> visited, List<Integer> component) {
        visited.add(node);
        component.add(node);
        for (int k : adj.get(node)) {
            if (!visited.contains(k)) {
                dfsComponent(k, adj, visited, component);
            }
        }
    }

    /**
     * Returneaza toate componentele conexe.
     */
    private List<List<Integer>> getAllCommunities(Map<Integer, List<Integer>> adj) {
        List<List<Integer>> communities = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (Integer userId : adj.keySet()) {
            if (!visited.contains(userId)) {
                List<Integer> component = new ArrayList<>();
                dfsComponent(userId, adj, visited, component);
                communities.add(component);
            }
        }
        return communities;
    }

    /**
     * Returneaza numarul de componente conexe
     */
    public int getNumberOfCommunities() {
        Map<Integer, List<Integer>> adj = buildGraph();
        return getAllCommunities(adj).size();
    }

    /**
     * Calculeaza cel mai lung drum simplu care porneste dintr-un nod
     */
    private int longestPathFrom(int current, Map<Integer, List<Integer>> adj, Set<Integer> visitedInPath) {
        visitedInPath.add(current);
        int maxDist = 0;

        for (int neighbor : adj.getOrDefault(current, Collections.emptyList())) {
            if (!visitedInPath.contains(neighbor)) {
                int dist = 1 + longestPathFrom(neighbor, adj, visitedInPath);
                if (dist > maxDist) {
                    maxDist = dist;
                }
            }
        }

        visitedInPath.remove(current);
        return maxDist;
    }

    /**
     * Returneaza cea mai sociabila comunitate .
     */
    public List<User> getMostSociableCommunity() {
        Map<Integer, List<Integer>> adj = buildGraph();
        List<List<Integer>> communities = getAllCommunities(adj);

        List<Integer> bestCommunityIds = new ArrayList<>();
        int maxPathOverall = -1;

        for (List<Integer> community : communities) {
            int maxPathInCommunity = 0;
            for (int node : community) {
                int pathLen = longestPathFrom(node, adj, new HashSet<>());
                if (pathLen > maxPathInCommunity) {
                    maxPathInCommunity = pathLen;
                }
            }

            if (maxPathInCommunity > maxPathOverall) {
                maxPathOverall = maxPathInCommunity;
                bestCommunityIds = community;
            }
        }

        List<User> result = new ArrayList<>();
        for (int id : bestCommunityIds) {
            result.add(userRepo.getById(id));
        }
        return result;
    }

    /**
     *Sterge o prietenie
     */
    public Friendship RemoveFriendship(int uid1, int uid2) {
    for (Friendship f : friendshipRepo.getAll().values()) {
        if ((f.getUid1() == uid1 && f.getUid2() == uid2) || (f.getUid1() == uid2 && f.getUid2() == uid1)) {
            return friendshipRepo.remove(f.getId());
        }
    }
    throw new RepoException("Prietenia dintre acesti utilizatori nu exista!");
}
    /**
     *Returneaza toti utilizatorii
     */
    public Collection<User> get_all_users(){
        return userRepo.getAll().values();
    }

    /**
     * Returneaza toate prieteniile
     */
    public Collection<Friendship> get_all_friendships(){
        return friendshipRepo.getAll().values();
    }
}
