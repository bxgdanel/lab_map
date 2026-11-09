package Repo;

import Domain.Entity;
import Domain.User;
import Exceptions.RepoException;
import Validari.Validator;

import java.util.HashMap;

public class MemoryRepo<E extends Entity> implements Repo<E> {
    protected HashMap<Integer,E> mem;
    protected int highestKey;
    protected Validator valid;
    public MemoryRepo(Validator valid) {
        mem = new HashMap<Integer, E>();
        this.valid = valid;
        highestKey = 0;
    }
    public HashMap<Integer,E> getAll(){
        return mem;
    }
    public E getById(Integer id){
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
        mem.put(obj.getId(), obj);
        if (obj.getId() >= highestKey) {
            highestKey = obj.getId() + 1;
        }
        return obj;
    }
    public E add(E obj){
        valid.valid(obj);
            obj.setId(highestKey);
            mem.putIfAbsent(highestKey, obj);
            highestKey++;
            return obj;
    }
    public E remove(Integer id){
        E obj = mem.remove(id);
        if(obj == null)
            throw new IllegalArgumentException("Id-ul nu exista");
        return obj;
    }

}
