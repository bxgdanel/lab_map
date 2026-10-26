package Repo;

public interface EntityParser<E> {
    /**
     * Parseaza o entitate
     * @param line - string care urmeaza sa fie parcurs
     * @return E - entitatea creeata
     */
    E createEntity(String line);

    /**
     * Converteste in formatul csv o entitate
     * @param entity - obiectul care urmeaza sa fie convertit in csv
     * @return string sub forma unui csv
     */
    String toCsv(E entity);
}
