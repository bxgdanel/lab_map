package Validari;

import Domain.User;
import Exceptions.ValidationException;

public class UserValidator implements Validator<User>{
    public void valid(User obj){
        if(obj == null)
            throw new ValidationException("Entiatea nu poate fii null.");
        StringBuilder err = new StringBuilder();
        if(obj.getId()<0)
            err.append("Id-ul trebuie sa fie pozitiv");
        if(obj.getUserName().trim().isEmpty())
            err.append("Username-ul nu poate sa fie gol.");
        if(!err.isEmpty())
            throw new ValidationException(err.toString());
    }
}
