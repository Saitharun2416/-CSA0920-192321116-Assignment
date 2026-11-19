package a4.task6.dao;

/** Checked wrapper for any storage failure (hides SQLException from upper layers). */
public class DaoException extends Exception {
    public DaoException(String message, Throwable cause) { super(message, cause); }
}
