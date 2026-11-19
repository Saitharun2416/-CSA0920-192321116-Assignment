package task_a4;

import java.io.*;

/** TASK A4.1 - what happens if a non-serializable field is NOT marked transient. */
public class TransientDemo {

    static class FakeConnection { }                       // NOT Serializable, like java.sql.Connection

    static class BadResident implements Serializable {
        String name = "Arun";
        FakeConnection connection = new FakeConnection(); // missing 'transient'
    }

    static class GoodResident implements Serializable {
        String name = "Arun";
        transient FakeConnection connection = new FakeConnection();
    }

    public static void main(String[] args) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
            out.writeObject(new BadResident());
        } catch (NotSerializableException e) {
            System.out.println("Without transient -> NotSerializableException: " + e.getMessage());
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new GoodResident());
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            GoodResident g = (GoodResident) in.readObject();
            System.out.println("With transient    -> saved OK, name=" + g.name + ", connection=" + g.connection);
        }
    }
}
