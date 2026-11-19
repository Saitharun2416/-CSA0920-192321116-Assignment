package task_a3;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** TASK A3.2 - generic data-access interface. T = entity type, K = primary-key type. */
public interface Dao<T, K> {
    void insert(T entity) throws SQLException;
    Optional<T> findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(K id) throws SQLException;
}
