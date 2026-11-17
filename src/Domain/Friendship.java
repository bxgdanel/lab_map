package Domain;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

public class Friendship extends Entity<Tuple<Long,Long>> {
    private Long uid1;

    private Long uid2;
    private LocalDateTime data;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public Friendship(LocalDateTime data, Long uid1, Long uid2) {
        this.data = data;
        setId(new Tuple<>(Math.min(uid1, uid2), Math.max(uid1, uid2)));
        this.uid2 = uid2;
        this.uid1 = uid1;
    }

    /**
     * Getter pentru id-ul primului utilizator
     * @return int
     */
    public Long getUid1() {
        return uid1;
    }


    public Long getUid2() {
        return uid2;
    }


    public Friendship(Long uid1, Long uid2) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        data = LocalDateTime.now();
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
        return (uid1.equals(that.uid1) && uid2.equals(that.uid2)) ||
               (uid1.equals(that.uid2) && uid2.equals(that.uid1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.min(uid1, uid2), Math.max(uid1, uid2));
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
