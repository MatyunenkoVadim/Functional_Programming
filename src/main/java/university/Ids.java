package university;

import java.util.UUID;

public final class Ids {
    // DATA
    private Ids() {
    }

    // ACTION
    public static UUID newId() {
        return UUID.randomUUID();
    }
}
