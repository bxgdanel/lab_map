package Repo;

import Domain.Entity;
import Exceptions.RepoException;
import Validari.Validator;

import java.util.HashMap;

public class MemoryRepo<E extends Entity> implements Repo<E> {
    HashMap<Integer,E> mem;
    private int highestKey;
    private Validator valid;
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
    public E add(E obj){
        if(valid.valid(obj)) {
            obj.setId(highestKey);
            mem.putIfAbsent(highestKey, obj);
            highestKey++;
            return obj;
        }
        throw new RepoException("Datele invalide");
    }
    public E remove(Integer id){
        E obj = mem.remove(id);
        if(obj == null)
            throw new RepoException("Id-ul nu exista");
        return obj;
    }
}
