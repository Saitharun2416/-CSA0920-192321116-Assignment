package a3.q2;

/** Layer 1 - controller: the FINAL place where exceptions are handled and a message is shown. */
public class ExamController {
    private final ExamService service = new ExamService();
    public ExamService service() { return service; }

    /** Returns the message the student sees. */
    public String submit(String studentId, String fileName, long sizeKb, boolean diskFails, boolean keepCause) {
        try {
            service.submit(studentId, fileName, sizeKb, diskFails, keepCause);
            return "SUCCESS: " + fileName + " submitted";
        } catch (ExamWindowClosedException | NotRegisteredException | AnswerFileTooLargeException e) {
            return "REJECTED: " + e.getMessage();
        } catch (SubmissionStorageException e) {
            e.printStackTrace(System.out);                           // full chained trace goes to the log
            return "SORRY: " + e.getMessage() + ". Please try again later.";
        } catch (ExamException e) {
            return "ERROR: " + e.getMessage();
        } catch (InvalidSubmissionException e) {                     // unchecked, still handled at the top
            return "BUG: " + e.getMessage();
        }
    }
}
