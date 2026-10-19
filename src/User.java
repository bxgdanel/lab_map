import java.time.LocalDateTime;

public class User extends Entity {
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public String toString(){
        return getId() + "," + userName;
    }
}

