package Repo;

import Domain.Friendship;
import Domain.Tuple;
import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;

import java.sql.*;
import java.util.HashMap;

public class FriendshipDbRepository implements Repo<Tuple<Long,Long>,Friendship> {
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
    public HashMap<Tuple<Long,Long>,Friendship> getAll() {
       HashMap<Tuple<Long,Long>,Friendship> map = new HashMap<>();
       try(Connection con = DriverManager.getConnection(url,user,password);
           PreparedStatement ps = con.prepareStatement("select * from friendships");
       ) {
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
               Long uid1 = rs.getLong("id1");
               Long uid2 =rs.getLong("id2");
               Friendship f = new Friendship(rs.getTimestamp("friends_from").toLocalDateTime(),uid1,uid2);
               Tuple<Long,Long> i = new Tuple<Long,Long>(uid1,uid2);
               map.put(i,f);
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
        return map;
    }

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
