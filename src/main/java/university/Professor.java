package university;

import java.util.*;

public class Professor {
    private UUID id;
    private String name;
    private Set<UUID> courseIds;

    public Professor(String name) {
        this(Ids.newId(), name, Collections.emptySet());
    }

    Professor(UUID id, String name) {
        this(id, name, Collections.emptySet());
    }

    Professor(UUID id, String name, Collection<UUID> courseIds) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setName(name);
        // defensive copy on input
        this.courseIds = new LinkedHashSet<>(Objects.requireNonNull(courseIds));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");
        this.name = name.trim();
    }

    public void assignCourse(UUID courseId) {
        UUID id = Objects.requireNonNull(courseId);

        Set<UUID> copy = new LinkedHashSet<>(this.courseIds);
        copy.add(id);
        this.courseIds = copy;
    }

    public void unassignCourse(UUID courseId) {
        if (courseId == null) return;

        Set<UUID> copy = new LinkedHashSet<>(this.courseIds);
        copy.remove(courseId);
        this.courseIds = copy;
    }

    public Set<UUID> getCourseIds() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(courseIds));
    }

    public Professor withName(String newName) {
        return new Professor(this.id, newName, this.courseIds);
    }

    public Professor withAssignedCourse(UUID courseId) {
        UUID id = Objects.requireNonNull(courseId);

        Set<UUID> copy = new LinkedHashSet<>(this.courseIds);
        copy.add(id);
        return new Professor(this.id, this.name, copy);
    }

    public Professor withUnassignedCourse(UUID courseId) {
        if (courseId == null) return this;

        Set<UUID> copy = new LinkedHashSet<>(this.courseIds);
        copy.remove(courseId);
        return new Professor(this.id, this.name, copy);
    }

    @Override
    public String toString() {
        return "Professor{" + id + ", '" + name + "'}";
    }
}
