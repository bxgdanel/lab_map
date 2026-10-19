public interface Validator<E extends Entity> {
    public boolean valid(E obj);
}
