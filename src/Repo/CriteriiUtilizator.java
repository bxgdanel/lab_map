package Repo;

import Domain.Friendship;
import Domain.User;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CriteriiUtilizator {
    public static Predicate<User> hasLastName(String last){
        return u -> u.getUserName().contains(last);
    }
    public static Predicate<User> hasFriends(Repo<Friendship> frepo){
        Set<Integer> idWithFriend = frepo.getAll().values().stream().flatMap(f-> Stream.of(f.getUid1(),f.getUid2()))
                .collect(Collectors.toSet());
        return u->idWithFriend.contains(u.getId());
    }
    public static boolean hasValidUsername(User u){
        return u!=null && !u.getUserName().trim().isEmpty();
    }
        public static Predicate<User> withoutFriends(Repo<Friendship> frepo) {
        return hasFriends(frepo).negate();
    }
}
