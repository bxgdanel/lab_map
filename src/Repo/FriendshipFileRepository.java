package Repo;

import Domain.Friendship;
import Validari.Validator;

import java.time.LocalDateTime;

public class FriendshipFileRepository extends AbstractFileRepository<Friendship> {
    public FriendshipFileRepository(Validator valid, String filename) {
        super(valid, filename);
    }
    /**
     * Function that parses a line to create an entity
     * @param line - String to be parsed
     * @return entity wit the properties
     */
    @Override
    protected Friendship createEntity(String line) {
        String[] parts = line.split(";");
    Integer id1 = Integer.parseInt(parts[0].trim());
    Integer id2 = Integer.parseInt(parts[1].trim());

    LocalDateTime date = LocalDateTime.parse(parts[2].trim(), Friendship.FORMATTER);
    Friendship fr = new Friendship(date,id1,id2);
    fr.setId((highestKey));
    return fr;
    }
    /**
     * Function that converts an entity to a line
     * @param obj - entity to be converted
     * @return String - line containing the data
     */
    @Override
    protected String createLine(Friendship obj){
        return obj.getUid1() + ";" + obj.getUid2() + ";" + obj.getData().format(Friendship.FORMATTER);
    }
}
