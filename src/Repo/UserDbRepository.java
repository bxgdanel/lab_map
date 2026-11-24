package Repo;

import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;
import paging.Page;
import paging.PageRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDbRepository implements PagingRepository<Long,User> {
    private final Validator<User> validator;
    private final String url;
    private final String user;
    private final String password;
    public UserDbRepository(Validator<User> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Finds users using paging
     * @param pr - a page request
     * @return - a page containing users
     */
    @Override
    public Page<User> findAllOnPage(PageRequest pr){
        String sql = "Select id, username From users order by id Limit ? Offset ?";
        try(Connection con = DriverManager.getConnection(url,user,password)){
            int total;
            try(PreparedStatement cnt = con.prepareStatement("Select count(*) from users");
                ResultSet rs = cnt.executeQuery()){
                rs.next();
                total = rs.getInt(1);
            }

            List<User> items = new ArrayList<>();
            try(PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1,pr.pageSize());
                ps.setInt(2,pr.offset());
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        User u = new User(rs.getString("username"));
                        u.setId(rs.getLong("id"));
                        items.add(u);
                    }
                }
            }
            return new Page<>(items,total);
        } catch (SQLException e) {
            throw new RepoException("Eroare la paginare" + e.getMessage(),e);
        }
    }
    /**
     *
     * @return - a hashmap with al the users
     */
    @Override
    public HashMap<Long, User> getAll() {
        HashMap<Long,User> m = new HashMap<>();
       try(Connection con = DriverManager.getConnection(url,user,password);
           PreparedStatement ps  = con.prepareStatement("select * from users")){
           ResultSet rs = ps.executeQuery();
            while(rs.next()){
                User u = new User(rs.getString("username"));
                Long id = rs.getLong("id");
                u.setId(id);
                m.put(id,u);
            }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
       return m;
    }

    /**
     *
     * @param id - id of the user to be returned
     * @return user with the id given as parameter
     */
    @Override
    public User getById(Long id) {
        try(Connection con = DriverManager.getConnection(url,user,password);
            PreparedStatement ps  = con.prepareStatement("select * from users where id = ?")){
            ps.setLong(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                User u = new User(rs.getString("username"));
                Long id_ = rs.getLong("id");
                u.setId(id_);
                return u;
            }
        } catch (SQLException e) {
            throw new RepoException("Eroare" + e,e);
        }
        return null;
    }

    /**
     * adds a user
     * @param obj - object to be added
     * @return
     */
    @Override
    public User add(User obj) {
        validator.valid(obj);
        String sql = "INSERT INTO users(username) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, obj.getUserName());
            ps.executeUpdate();
             try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                obj.setId(keys.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RepoException("Eroare" + e,e);
        }
    return obj;
    }

    /**
     * removes the user
     * @param id - id of the user to be removed
     * @return
     */
        @Override
    public User remove(Long id) {
        User found = this.getById(id);
        if(found == null)
            return found;
       try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Eroare la delete: " + e.getMessage(), e);
        }
        return found;
    }

}
