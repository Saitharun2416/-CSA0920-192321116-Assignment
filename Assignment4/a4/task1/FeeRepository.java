package a4.task1;

/** Persistence abstraction: lets the service be tested without a database. */
public interface FeeRepository {
    void save(String regNo, double amount) throws Exception;
}
