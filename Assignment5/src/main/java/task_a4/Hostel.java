package task_a4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hostel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final List<Room> rooms = new ArrayList<>();

    public Hostel(String name) { this.name = name; }
    public void add(Room r) { rooms.add(r); }
    public String getName() { return name; }
    public List<Room> getRooms() { return rooms; }
}
