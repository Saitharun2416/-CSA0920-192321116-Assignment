package a3.q3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/** A BufferedReader that counts how many instances are currently open (to prove leaks). */
public class TrackedReader extends BufferedReader {
    static final AtomicInteger OPEN = new AtomicInteger();
    private boolean closed;
    private final String name;

    public TrackedReader(Path p) throws IOException {
        super(Files.newBufferedReader(p));
        this.name = p.getFileName().toString();
        OPEN.incrementAndGet();
    }
    public String name() { return name; }

    @Override public void close() throws IOException {
        if (!closed) { closed = true; OPEN.decrementAndGet(); }
        super.close();
    }
}
