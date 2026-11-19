package a4.task6.model;

/** Immutable data carrier for one registration row. */
public record Registration(String studentId, String studentName, String email, String eventName) { }
