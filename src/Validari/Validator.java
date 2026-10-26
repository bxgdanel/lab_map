package Validari;

import Domain.Entity;

public interface Validator<E extends Entity> {
    /**
     * Verifica validitatea obiectului
     * @param obj
     * @return
     */
    public boolean valid(E obj);
}
