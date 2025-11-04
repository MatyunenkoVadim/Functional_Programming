package university;

import java.util.*;

public class Student {
    // DATA
    private UUID id;
    private String name;
    private final Set<UUID> enrollmentIds = new LinkedHashSet<>();

    // ACTION
    public Student(String name) {
        this.id = Ids.newId();
        setName(name);
    }

    // ACTION
    Student(UUID id, String name) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setName(name);
    }

    // CALCULATION
    public UUID getId() {
        return id;
    }

    // CALCULATION
    public String getName() {
        return name;
    }

    // ACTION
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");
        this.name = name.trim();
    }

    // ACTION
    public void addEnrollment(UUID enrollmentId) {
        enrollmentIds.add(Objects.requireNonNull(enrollmentId));
    }

    // ACTION
    public void removeEnrollment(UUID enrollmentId) {
        enrollmentIds.remove(enrollmentId);
    }

    // CALCULATION
    public Set<UUID> getEnrollmentIds() {
        return Collections.unmodifiableSet(enrollmentIds);
    }

    // CALCULATION
    @Override
    public String toString() {
        return "Student{" + id + ", '" + name + "'}";
    }
}
