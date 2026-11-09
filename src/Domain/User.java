package Domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class User extends Entity {
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    /**
     * getter username
     * @return String- username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * setter username
     * @param userName - string, username ul care urmeaza sa fie setat
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public String toString(){
        return getId() + "," + userName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() == user.getId() && Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), userName);
    }
    public static User createEntity(String line,String separator) {
        String[] el = line.split(Pattern.quote(separator));
        User user = new User(el[1]);
        user.setId(Integer.parseInt(el[0]));
        return user;
    }
    @Override
    public String toSave(String separator) {
        return this.getId() + separator + this.getUserName();
    }
}

