import org.w3c.dom.Entity;

public class UserParser implements EntityParser<User> {

    @Override
    public User createEntity(String line) {
        String[] el = line.split(",");
        User user = new User(el[1]);
        user.setId(Integer.parseInt(el[0]));
        return user;
    }

    @Override
    public String toCsv(User entity) {
        return entity.getId() + "," + entity.getUserName();
    }
}
