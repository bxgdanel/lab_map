package Repo;

import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;

import java.util.regex.Pattern;

public class UserFileRepository extends AbstractFileRepository<User>{
    public UserFileRepository(Validator valid, String filename) {
        super(valid, filename);
    }

    /**
     * Function that parses a line to create an entity
     * @param line - String to be parsed
     * @return entity wit the properties
     * @throws IllegalArgumentException
     */
    @Override
    protected User createEntity(String line) {
        try {
            String[] el = line.split(Pattern.quote(";"));
            User user = new User(el[1]);
            user.setId(Integer.parseInt(el[0]));
            return user;
        }catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
            throw new IllegalArgumentException("Datele din fisier sunt invalide",e);
        }
    }

    /**
     * Function that converts an entity to a line
     * @param obj - entity to be converted
     * @return String - line containing the data
     */
    @Override
    protected String createLine(User obj){
        return obj.getId() + ";" + obj.getUserName();
    }

}
