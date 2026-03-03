package university;

import java.util.*;

public final class Course {
    private final UUID id;
    private final String title;
    private final int credits;
    private final UUID professorId; // может быть null
    private final Set<UUID> enrollmentIds;

    public Course(String title, int credits) {
        this(Ids.newId(), title, credits, null, Collections.emptySet());
    }

    Course(UUID id, String title, int credits, UUID professorId) {
        this(id, title, credits, professorId, Collections.emptySet());
    }

    Course(UUID id, String title, int credits, UUID professorId, Collection<UUID> enrollmentIds) {
        if (id == null) throw new IllegalArgumentException("id");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title");
        if (credits <= 0) throw new IllegalArgumentException("credits");

        this.id = id;
        this.title = title.trim();
        this.credits = credits;
        this.professorId = professorId;

        Collection<UUID> src = (enrollmentIds == null) ? Collections.emptySet() : enrollmentIds;
        this.enrollmentIds = Collections.unmodifiableSet(new LinkedHashSet<>(src));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public Optional<UUID> getProfessorId() {
        return Optional.ofNullable(professorId);
    }

    public Set<UUID> getEnrollmentIds() {
        return enrollmentIds;
    }

    public Course withTitle(String newTitle) {
        return new Course(this.id, newTitle, this.credits, this.professorId, this.enrollmentIds);
    }

    public Course withCredits(int newCredits) {
        return new Course(this.id, this.title, newCredits, this.professorId, this.enrollmentIds);
    }

    public Course withProfessor(UUID newProfessorId) {
        return new Course(this.id, this.title, this.credits, Objects.requireNonNull(newProfessorId, "professorId"), this.enrollmentIds);
    }

    public Course withoutProfessor() {
        return new Course(this.id, this.title, this.credits, null, this.enrollmentIds);
    }

    public Course withAddedEnrollment(UUID enrollmentId) {
        UUID eid = Objects.requireNonNull(enrollmentId, "enrollmentId");

        LinkedHashSet<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.add(eid);
        return new Course(this.id, this.title, this.credits, this.professorId, copy);
    }

    public Course withRemovedEnrollment(UUID enrollmentId) {
        if (enrollmentId == null) return this;

        LinkedHashSet<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.remove(enrollmentId);
        return new Course(this.id, this.title, this.credits, this.professorId, copy);
    }

    @Override
    public String toString() {
        return "Course{" + id + ", '" + title + "', " + credits + " кр.}";
    }
}
