public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public boolean valid(Friendship obj) {
       return obj.getUid1() != obj.getUid2() && obj.getUid1()>=0 && obj.getUid2()>=0;
    }
}
