import static org.junit.jupiter.api.Assertions.*;

class MemoryRepoTest {

    @org.junit.jupiter.api.Test
    void remove() {
        MemoryRepo<User> da = new MemoryRepo<>(new UserValidator());
        da.remove(2);
    }
}