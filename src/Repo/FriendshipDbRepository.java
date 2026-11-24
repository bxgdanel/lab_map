package Repo;

import Domain.Friendship;
import Domain.Tuple;
import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;
import paging.Page;
import paging.PageRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendshipDbRepository implements FriendshipPagingRepo {
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

    /***
     * Finds all friends using paging
     * @param userId - id of the user whose friends to search for
     * @param pr - a page request
     * @return a page containing friends of the user with userId
     */
    public Page<User> findFriendsOnPage(Long userId, PageRequest pr){
        String sql = """
                SELECT u.id, u.username
                FROM friendships f
                JOIN users u on u.id = CASE WHEN f.id1 = ? THEN f.id2 ELSE f.id1 END
                WHERE f.id1 = ? OR f.id2 = ?
                ORDER BY u.id LIMIT ? OFFSET ?""";
        try(Connection con = DriverManager.getConnection(url,user,password)){
            int total;
            try(PreparedStatement cnt = con.prepareStatement("select count(*) from friendships where id1 = ? or id2 = ?")){
                cnt.setLong(1,userId);
                cnt.setLong(2,userId);
                try(ResultSet rs = cnt.executeQuery()){rs.next(); total = rs.getInt(1);}
            }
        List<User> items = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId); ps.setLong(2, userId); ps.setLong(3, userId);
            ps.setInt(4, pr.pageSize());
            ps.setInt(5, pr.offset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User(rs.getString("username"));
                    u.setId(rs.getLong("id"));
                    items.add(u);
                }
            }
        }
        return new Page<>(items, total);
        }catch (SQLException e) {
            throw new RepoException("Eroare la paginarea prieteniei: " + e.getMessage(),e);
        }
    }
    /**
     *
     * @return returns a map with all the items
     */
    @Override
    public HashMap<Tuple<Long,Long>,Friendship> getAll() {
       HashMap<Tuple<Long,Long>,Friendship> map = new HashMap<>();
       try(Connection con = DriverManager.getConnection(url,user,password);
           PreparedStatement ps = con.prepareStatement("select * from friendships");
       ) {
           ResultSet rs = ps.executeQuery();
           while (rs.next()) {
               Long uid1 = rs.getLong("id1");
               Long uid2 = rs.getLong("id2");
               Friendship f = new Friendship(rs.getTimestamp("friends_from").toLocalDateTime(), uid1, uid2);
               Tuple<Long, Long> i = new Tuple<Long, Long>(uid1, uid2);
               map.put(i, f);
           }

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
        return map;
    }

    /**
     *
     * @param id - id of the friendship to find
     * @return returns the friendship with the id given as parameter
     */
    @Override
    public Friendship getById(Tuple<Long,Long> id) {
        try(Connection con = DriverManager.getConnection(url,user,password);
            PreparedStatement ps  = con.prepareStatement("select * from friendships where id1 = ? and id2 = ?")){
        ps.setLong(1,id.getLeft());
        ps.setLong(2,id.getRight());
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Long id1 = rs.getLong("id1");
            Long id2 = rs.getLong("id2");
            Friendship f = new Friendship(rs.getTimestamp("friends_from").toLocalDateTime(),id1,id2);
            f.setId(new Tuple<Long,Long>(id1,id2));
            return f;
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
        return null;
    }

    /**
     * Adds a friendship
     * @param obj - object to pe added
     * @return returns the object that has been added with its assigned id
     */
    @Override
    public Friendship add(Friendship obj) {
        String sql = "INSERT INTO friendships(id1,id2,friends_from) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,obj.getUid1());
            ps.setLong(2,obj.getUid2());
            ps.setTimestamp(3,Timestamp.valueOf(obj.getData()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                obj.setId(new Tuple<Long,Long>(keys.getLong("id1"), keys.getLong("id2")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * Removes a friendship
     * @param id - id of the friendship to be removed
     * @return the object that has been removed
     */
    @Override
    public Friendship remove(Tuple<Long,Long> id) {
        Friendship found = this.getById(id);
        if(found == null)
            return found;
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = connection.prepareStatement("DELETE FROM friendships WHERE id1 = ? and id2 = ?")) {
            ps.setLong(1, id.getLeft());
            ps.setLong(2,id.getRight());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Eroare la delete: " + e.getMessage(), e);
        }
        return found;
    }

}
