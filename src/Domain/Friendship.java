package Domain;

import java.util.Objects;

public class Friendship extends Entity{
    private int uid1;

    private int uid2;
    /**
     * Getter pentru id-ul primului utilizator
     * @return int
     */
    public int getUid1() {
        return uid1;
    }

    /**
     * setter pentru id-ul primului utilizator
     * @param uid1 - id-ul care urmeaza sa fie setat
     */
    public void setUid1(int uid1) {
        this.uid1 = uid1;
    }
    /**
     * Getter pentru id-ul celui de al 2 lea utilizator
     * @return int
     */
    public int getUid2() {
        return uid2;
    }
    /**
     * setter pentru id-ul celui de al 2 lea utilizator
     * @param uid2 - id-ul care urmeaza sa fie setat
     */
    public void setUid2(int uid2) {
        this.uid2 = uid2;
    }

    public Friendship(int uid1, int uid2) {
        this.uid1 = uid1;
        this.uid2 = uid2;
    }

    @Override
    public String toString(){
        return getId() + "," + uid1 + "," + uid2;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (uid1 == that.uid1 && uid2 == that.uid2) ||
               (uid1 == that.uid2 && uid2 == that.uid1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.min(uid1, uid2), Math.max(uid1, uid2));
    }
}
