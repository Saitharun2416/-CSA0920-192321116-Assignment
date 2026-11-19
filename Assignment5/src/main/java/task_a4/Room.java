package task_a4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int roomNo;
    private final List<Resident> residents = new ArrayList<>();

    public Room(int roomNo) { this.roomNo = roomNo; }
    public void add(Resident r) { residents.add(r); }
    public int getRoomNo() { return roomNo; }
    public List<Resident> getResidents() { return residents; }
}
