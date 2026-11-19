package a4.task6.service;

import a4.task6.dao.DaoException;
import a4.task6.dao.DuplicateRegistrationException;
import a4.task6.dao.RegistrationDao;
import a4.task6.model.Registration;

import java.util.List;
import java.util.regex.Pattern;

/** Service layer: validation + rules. No Swing and no SQL here. */
public class RegistrationService {
    public static final int PAGE_SIZE = 10;
    private static final Pattern ID = Pattern.compile("[A-Za-z0-9]{6,12}");
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    private final RegistrationDao dao;

    public RegistrationService(RegistrationDao dao) { this.dao = dao; }

    public List<String> events() throws DaoException { return dao.events(); }

    /** Pseudocode: validate each field -> dao.insert (duplicate guaranteed by DB UNIQUE key too). */
    public void register(String id, String name, String email, String event)
            throws ValidationException, DuplicateRegistrationException, DaoException {
        if (id == null || !ID.matcher(id.trim()).matches()) throw new ValidationException("Student ID must be 6-12 letters/digits");
        if (name == null || name.isBlank() || name.length() > 60) throw new ValidationException("Name is required (max 60 characters)");
        if (email == null || !EMAIL.matcher(email.trim()).matches()) throw new ValidationException("Email address is not valid");
        if (event == null || event.equals("All")) throw new ValidationException("Choose an event");
        dao.insert(new Registration(id.trim(), name.trim(), email.trim(), event));
    }

    public List<Registration> page(String event, int pageNo) throws DaoException { return dao.page(event, PAGE_SIZE, pageNo * PAGE_SIZE); }
    public int pageCount(String event) throws DaoException { return Math.max(1, (dao.count(event) + PAGE_SIZE - 1) / PAGE_SIZE); }
    public List<Registration> all(String event) throws DaoException { return dao.all(event); }
}
