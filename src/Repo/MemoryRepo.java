package Repo;

import Domain.Entity;
import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;

import java.util.HashMap;

public class MemoryRepo<ID,E extends Entity<ID>> implements Repo<ID,E> {
    protected HashMap<ID,E> mem;
    protected Long highestKey;
    protected Validator valid;
    public MemoryRepo(Validator valid) {
        mem = new HashMap<ID, E>();
        this.valid = valid;
        highestKey = 0L;
    }
    public HashMap<ID,E> getAll(){
        return mem;
    }
    public E getById(ID id){
        E aux = mem.get(id);
        if(aux != null)
            return aux;
        throw new RepoException("Nu exista un utilizator cu id ul dat");
    }
      /**
     * Pastreaza ID-ul citit, trece prin validator si actualizeaza highestKey.
     */
    public E saveExisting(E obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi nula!");
        }
        valid.valid(obj);
        mem.put((ID) obj.getId(), obj);
        if (obj.getId() instanceof Long l && l >= highestKey) highestKey = l + 1;
        return obj;
    }
    public E add(E obj) {
        valid.valid(obj);
        if (obj.getId() == null) obj.setId(generateId());
        mem.putIfAbsent((ID) obj.getId(), obj);
        return obj;
    }
    protected ID generateId() { return (ID) (Long) highestKey++; }
    public E remove(ID id){
        E obj = mem.remove(id);
        if(obj == null)
            throw new RepoException("Id-ul nu exista");
        return obj;
    }

}
