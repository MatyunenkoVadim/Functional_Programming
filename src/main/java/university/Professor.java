package university;

import java.util.*;

public final class Professor {
    private final UUID id;
    private final String name;
    private final Set<UUID> courseIds;

    public Professor(String name) {
        this(Ids.newId(), name, Collections.emptySet());
    }

    Professor(UUID id, String name) {
        this(id, name, Collections.emptySet());
    }

    Professor(UUID id, String name, Collection<UUID> courseIds) {
        if (id == null) throw new IllegalArgumentException("id");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");

        this.id = id;
        this.name = name.trim();

        Collection<UUID> src = (courseIds == null) ? Collections.emptySet() : courseIds;
        this.courseIds = Collections.unmodifiableSet(new LinkedHashSet<>(src));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getCourseIds() {
        return courseIds;
    }

    public Professor withName(String newName) {
        return new Professor(this.id, newName, this.courseIds);
    }

    public Professor withAssignedCourse(UUID courseId) {
        UUID cid = Objects.requireNonNull(courseId, "courseId");

        LinkedHashSet<UUID> copy = new LinkedHashSet<>(this.courseIds);
        copy.add(cid);
        return new Professor(this.id, this.name, copy);
    }

    public Professor withUnassignedCourse(UUID courseId) {
        if (courseId == null) return this;

        LinkedHashSet<UUID> copy = new LinkedHashSet<>(this.courseIds);
        copy.remove(courseId);
        return new Professor(this.id, this.name, copy);
    }

    @Override
    public String toString() {
        return "Professor{" + id + ", '" + name + "'}";
    }
}
