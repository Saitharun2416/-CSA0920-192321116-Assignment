package a4.task6.dao;

import a4.task6.model.Registration;
import java.util.List;

/** Data-access layer contract. Implemented by JDBC (MySQL) and by an in-memory fallback. */
public interface RegistrationDao {
    List<String> events() throws DaoException;
    void insert(Registration r) throws DuplicateRegistrationException, DaoException;
    /** event == null or "All" means no filter. */
    List<Registration> page(String event, int limit, int offset) throws DaoException;
    int count(String event) throws DaoException;
    List<Registration> all(String event) throws DaoException;
}
