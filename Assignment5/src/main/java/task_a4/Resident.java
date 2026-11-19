package task_a4;

import java.io.Serializable;
import java.sql.Connection;

/**
 * TASK A4.1 / A4.2.
 *  - password   : transient  -> secret, must never be written to disk
 *  - connection : transient  -> a live Connection is not Serializable and is meaningless after reload
 *  - serialVersionUID is fixed so that compatible changes (adding a field) do not break old files.
 *
 * VERSIONING EXPERIMENT (A4.2):
 *   Step 1: run HostelSave                      (writes hostel.ser with this class as is)
 *   Step 2: uncomment the 3 lines marked  // STEP 2   then run HostelLoad
 *           -> old file loads fine, phone = null  (same serialVersionUID, compatible change)
 *   Step 3: change serialVersionUID to 2L then run HostelLoad
 *           -> java.io.InvalidClassException: local class incompatible
 */
public class Resident implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String regNo;
    private transient String password;
    private transient Connection connection;
    // private String phone;                                   // STEP 2: uncomment

    public Resident(String name, String regNo, String password, Connection connection) {
        this.name = name;
        this.regNo = regNo;
        this.password = password;
        this.connection = connection;
    }

    public String getName() { return name; }
    public String getRegNo() { return regNo; }

    @Override
    public String toString() {
        return "Resident{" + name + ", " + regNo
                + ", password=" + password
                + ", connection=" + (connection == null ? "null" : "live")
                // + ", phone=" + phone                        // STEP 2: uncomment
                + "}";
    }
}
