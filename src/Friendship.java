public class Friendship extends Entity{
    private int uid1;

    public int getUid1() {
        return uid1;
    }

    public void setUid1(int uid1) {
        this.uid1 = uid1;
    }

    public int getUid2() {
        return uid2;
    }

    public void setUid2(int uid2) {
        this.uid2 = uid2;
    }

    public Friendship(int uid1, int uid2) {
        this.uid1 = uid1;
        this.uid2 = uid2;
    }

    private int uid2;
    @Override
    public String toString(){
        return getId() + "," + uid1 + "," + uid2;
    }
}
