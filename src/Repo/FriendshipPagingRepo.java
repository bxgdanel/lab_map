package Repo;

import Domain.Friendship;
import Domain.Tuple;
import Domain.User;
import paging.Page;
import paging.PageRequest;

public interface FriendshipPagingRepo extends Repo<Tuple<Long,Long>, Friendship> {
    /**
     * Finds friends of a user using paging
     * @param userId - id of user to search friends of
     * @param pr - page request
     * @return - a page containing users
     */
    public Page<User> findFriendsOnPage(Long userId, PageRequest pr);

}
