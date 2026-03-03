package university;

import java.util.*;

public final class Student {
    private final UUID id;
    private final String name;
    private final Set<UUID> enrollmentIds;

    public Student(String name) {
        this(Ids.newId(), name, Collections.emptySet());
    }

    Student(UUID id, String name) {
        this(id, name, Collections.emptySet());
    }

    Student(UUID id, String name, Collection<UUID> enrollmentIds) {
        if (id == null) throw new IllegalArgumentException("id");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");

        this.id = id;
        this.name = name.trim();

        Collection<UUID> src = (enrollmentIds == null) ? Collections.emptySet() : enrollmentIds;
        this.enrollmentIds = Collections.unmodifiableSet(new LinkedHashSet<>(src));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getEnrollmentIds() {
        return enrollmentIds;
    }

    public Student withName(String newName) {
        return new Student(this.id, newName, this.enrollmentIds);
    }

    public Student withAddedEnrollment(UUID enrollmentId) {
        UUID eid = Objects.requireNonNull(enrollmentId, "enrollmentId");

        LinkedHashSet<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.add(eid);
        return new Student(this.id, this.name, copy);
    }

    public Student withRemovedEnrollment(UUID enrollmentId) {
        if (enrollmentId == null) return this;

        LinkedHashSet<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.remove(enrollmentId);
        return new Student(this.id, this.name, copy);
    }

    @Override
    public String toString() {
        return "Student{" + id + ", '" + name + "'}";
    }
}
