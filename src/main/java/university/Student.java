package university;

import java.util.*;

public class Student {
    private UUID id;
    private String name;
    private Set<UUID> enrollmentIds;

    public Student(String name) {
        this(Ids.newId(), name, Collections.emptySet());
    }

    Student(UUID id, String name) {
        this(id, name, Collections.emptySet());
    }

    Student(UUID id, String name, Collection<UUID> enrollmentIds) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setName(name);
        this.enrollmentIds = new LinkedHashSet<>(Objects.requireNonNull(enrollmentIds));
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

    public void addEnrollment(UUID enrollmentId) {
        UUID id = Objects.requireNonNull(enrollmentId);

        Set<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.add(id);
        this.enrollmentIds = copy;
    }

    public void removeEnrollment(UUID enrollmentId) {
        if (enrollmentId == null) return;

        Set<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.remove(enrollmentId);
        this.enrollmentIds = copy;
    }

    public Set<UUID> getEnrollmentIds() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(enrollmentIds));
    }

    public Student withName(String newName) {
        return new Student(this.id, newName, this.enrollmentIds);
    }

    public Student withAddedEnrollment(UUID enrollmentId) {
        UUID id = Objects.requireNonNull(enrollmentId);

        Set<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.add(id);
        return new Student(this.id, this.name, copy);
    }

    public Student withRemovedEnrollment(UUID enrollmentId) {
        if (enrollmentId == null) return this;

        Set<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.remove(enrollmentId);
        return new Student(this.id, this.name, copy);
    }

    @Override
    public String toString() {
        return "Student{" + id + ", '" + name + "'}";
    }
}
