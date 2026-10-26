package Repo;

import Domain.Friendship;

public class FriendshipParser implements EntityParser<Friendship> {
    @Override
    public Friendship createEntity(String line) {
        String[] el = line.split(",");
        Friendship fr = new Friendship(Integer.parseInt(el[1]),Integer.parseInt(el[2]));
        fr.setId(Integer.parseInt(el[0]));
        return fr;
    }

    @Override
    public String toCsv(Friendship entity) {
        return entity.getId() + "," + entity.getUid1() + "," + entity.getUid2();
    }
}
