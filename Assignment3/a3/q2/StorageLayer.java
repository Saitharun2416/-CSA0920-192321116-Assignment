package a3.q2;

import java.io.IOException;

/** Layer 3 - storage. Throws the low-level IOException, converts it into a domain exception. */
public class StorageLayer {
    static final long LIMIT_KB = 500;

    public void save(String fileName, long sizeKb, boolean diskFails, boolean keepCause)
            throws SubmissionStorageException, AnswerFileTooLargeException {
        if (fileName == null || fileName.isBlank()) throw new InvalidSubmissionException("File name is empty");
        if (sizeKb > LIMIT_KB) throw new AnswerFileTooLargeException(fileName + " is " + sizeKb + " KB; limit is " + LIMIT_KB + " KB");
        try {
            if (diskFails) throw new IOException("Disk quota exceeded");
            System.out.println("   [storage] saved " + fileName);
        } catch (IOException e) {
            if (keepCause) throw new SubmissionStorageException("Could not store " + fileName, e);  // chained
            throw new SubmissionStorageException("Could not store " + fileName);                    // cause LOST
        }
    }
}
