import java.util.HashMap;

public interface Repo<E extends Entity> {

    public HashMap<Integer,E> getAll();
    public E getById(Integer id);
    public E add(E obj);
    public E remove(Integer id);
}
