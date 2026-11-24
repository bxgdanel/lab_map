package Repo;

import Domain.Entity;
import Domain.Friendship;
import Domain.Tuple;
import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

public class Repositoryfactory {
    private static final String CONFIG_PATH = "src/main/resources/config.properties";
    private final Properties props = new Properties();
    private final RepositoryType repoType;
    private final Map<RepositoryType, Function<Validator<User>,Repo<Long,User>>> userRepoMap = new EnumMap<>(RepositoryType.class);
    private final Map<RepositoryType, Function<Validator<Friendship>,Repo<Tuple<Long,Long>,Friendship>>> friendshipRepoMap = new EnumMap<>(RepositoryType.class);
    public Repositoryfactory() {
        try(InputStream input = new FileInputStream(CONFIG_PATH)){
            props.load(input);
        }catch (IOException e){
            throw new RuntimeException("Nu s-a putut incarca fisierul de properties" + CONFIG_PATH,e);
        }

        String typeStr = getRequiredProperty("repository.type");
        try{
            this.repoType = RepositoryType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Valoare nerecunoscuta pentru repository type" + typeStr,e);
        }
        userRepoMap.put(RepositoryType.MEMORY,MemoryRepo::new);
        userRepoMap.put(RepositoryType.FILE,val-> new UserFileRepository(val,getRequiredProperty("file.users")));
        userRepoMap.put(RepositoryType.DATABASE,val-> new UserDbRepository(
                val,
                getRequiredProperty("db.url"),
        getRequiredProperty("db.user"),
        getRequiredProperty("db.password")
        ));
        friendshipRepoMap.put(RepositoryType.MEMORY,MemoryRepo::new);
        friendshipRepoMap.put(RepositoryType.FILE,val-> new FriendshipFileRepository(val,getRequiredProperty("file.friendships")));
        friendshipRepoMap.put(RepositoryType.DATABASE,val-> new FriendshipDbRepository(
                val,
                getRequiredProperty("db.url"),
                getRequiredProperty("db.user"),
                getRequiredProperty("db.password")
        ));

    }

    /**
     * Helper function that verifies if the required property is given in the file
     * @param s - property key
     * @return
     */
    private String getRequiredProperty(String s) {
        String value = props.getProperty(s);
        if(value == null || value.trim().isEmpty())
            throw new IllegalArgumentException("Proprietatea obligatorie: " + s + " nu a fost gasita in fisierul config.properties");
        return value.trim();
    }

    /**
     * geter for PageSize
     * @return - integer representing the pagesize set in the config file
     */
    public Integer getPageSize(){
        try{
            return Integer.parseInt(getRequiredProperty("db.pagesize"));
        }catch(NumberFormatException e){
            throw new RepoException("Format la pagesize invalid",e);
        }
    }
    /**
     * Function that creates a user repository based on the config
     * @param val - validator
     * @return - Repo<User>
     */
    public Repo<Long,User> createUserRepository(Validator<User> val){
        var mapper =userRepoMap.get(repoType);
        if(mapper == null)  throw new IllegalArgumentException("Tip invalid"+repoType);

        Supplier<Repo<Long,User>> supplier = () -> mapper.apply(val);
        return supplier.get();
    }
    /**
     * Function that creates a friendship repository based on the config
     * @param val - validator
     * @return - Repo<Friendship>
     */
    public Repo<Tuple<Long,Long>,Friendship> createFriendshipRepository(Validator<Friendship> val){
        var mapper =friendshipRepoMap.get(repoType);
        if(mapper == null)  throw new IllegalArgumentException("Tip invalid"+repoType);

        Supplier<Repo<Tuple<Long,Long>,Friendship>> supplier = () -> mapper.apply(val);
        return supplier.get();
    }
}
