public interface EntityParser<E> {
    E createEntity(String line);
    String toCsv(E entity);
}
