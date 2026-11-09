package Repo;

import Domain.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Repo<E extends Entity> {

    /**
     * Get all
     * @return map-ul cu toate elementele
     */
    public HashMap<Integer,E> getAll();

    /**
     * Returneaza obiectul cu id-ul id
     * @param id
     * @return obiectul cu id-ul dat ca parametru
     */
    public E getById(Integer id);

    /**
     * Functia de add
     * @param obj - obiectul care urmeaza sa fie adaugat
     * @return obiectul tocmai adaugat cu id-ul care i a fost asociat
     */
    public E add(E obj);

    /**
     * Functia de remove
     * @param id
     * @return obiectul eliminat
     */
    public E remove(Integer id);
    default List<E> findAll(Predicate<E> filter){
        return getAll().values().stream().filter(filter).collect(Collectors.toList());
    }
}
