package Domain;

import java.util.Objects;

public abstract class Entity<ID> {
    private ID id;

    /**
     * Getter id
     * @return int-id
     */
    public ID getId() {
        return id;
    }

    /**
     * Setter id
     * @param id -int
     */
    public void setId(ID id) {
        this.id = id;
    }
    public String toSave(String separator){ return String.valueOf(this.id);}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id,entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
