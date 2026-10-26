package Validari;

import Domain.User;

public class UserValidator implements Validator<User>{
    public boolean valid(User obj){
        return obj != null && obj.getUserName()!=null && !obj.getUserName().trim().isEmpty() ;
    }
}
