package Validari;

import Domain.Entity;

@FunctionalInterface
public interface Validator<E extends Entity> {
    /**
     * Verifica validitatea obiectului
     * @param obj
     */
    public void valid(E obj);
    public default Validator<E> andThen(Validator<E> other){
        return obj ->{
            this.valid(obj);
            other.valid(obj);
        };
    }
}
