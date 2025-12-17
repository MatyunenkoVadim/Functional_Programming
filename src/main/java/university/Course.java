package university;

import java.util.*;

public class Course {
    private UUID id;
    private String title;
    private int credits;
    private UUID professorId; // может быть null
    private Set<UUID> enrollmentIds;

    public Course(String title, int credits) {
        this(Ids.newId(), title, credits, null, Collections.emptySet());
    }

    Course(UUID id, String title, int credits, UUID professorId) {
        this(id, title, credits, professorId, Collections.emptySet());
    }

    Course(UUID id, String title, int credits, UUID professorId, Collection<UUID> enrollmentIds) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setTitle(title);
        setCredits(credits);
        this.professorId = professorId;
        this.enrollmentIds = new LinkedHashSet<>(Objects.requireNonNull(enrollmentIds));
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

    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title");
        this.title = title.trim();
    }

    void setCredits(int credits) {
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    public Optional<UUID> getProfessorId() {
        return Optional.ofNullable(professorId);
    }

    public void setProfessor(UUID professorId) {
        this.professorId = Objects.requireNonNull(professorId);
    }

    public void clearProfessor() {
        this.professorId = null;
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

    public Course withTitle(String newTitle) {
        return new Course(this.id, newTitle, this.credits, this.professorId, this.enrollmentIds);
    }

    public Course withCredits(int newCredits) {
        return new Course(this.id, this.title, newCredits, this.professorId, this.enrollmentIds);
    }

    public Course withProfessor(UUID newProfessorId) {
        return new Course(this.id, this.title, this.credits, Objects.requireNonNull(newProfessorId), this.enrollmentIds);
    }

    public Course withoutProfessor() {
        return new Course(this.id, this.title, this.credits, null, this.enrollmentIds);
    }

    public Course withAddedEnrollment(UUID enrollmentId) {
        UUID id = Objects.requireNonNull(enrollmentId);

        Set<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.add(id);
        return new Course(this.id, this.title, this.credits, this.professorId, copy);
    }

    public Course withRemovedEnrollment(UUID enrollmentId) {
        if (enrollmentId == null) return this;

        Set<UUID> copy = new LinkedHashSet<>(this.enrollmentIds);
        copy.remove(enrollmentId);
        return new Course(this.id, this.title, this.credits, this.professorId, copy);
    }

    @Override
    public String toString() {
        return "Course{" + id + ", '" + title + "', " + credits + " кр.}";
    }
}
