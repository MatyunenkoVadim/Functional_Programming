package university;

import java.util.*;

public class Professor {
    // DATA
    private UUID id;
    private String name;
    private final Set<UUID> courseIds = new LinkedHashSet<>();

    // ACTION
    public Professor(String name) {
        this.id = Ids.newId();
        setName(name);
    }

    // ACTION
    Professor(UUID id, String name) {
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
    public void assignCourse(UUID courseId) {
        courseIds.add(Objects.requireNonNull(courseId));
    }

    // ACTION
    public void unassignCourse(UUID courseId) {
        courseIds.remove(courseId);
    }

    // CALCULATION
    public Set<UUID> getCourseIds() {
        return Collections.unmodifiableSet(courseIds);
    }

    // CALCULATION
    @Override
    public String toString() {
        return "Professor{" + id + ", '" + name + "'}";
    }
}
