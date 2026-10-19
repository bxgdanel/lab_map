public class UserValidator implements Validator<User>{
    public boolean valid(User obj){
        return obj.getUserName() != null;
    }
}
