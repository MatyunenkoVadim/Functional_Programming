package university;

import java.util.*;

public class Course {
    private UUID id;
    private String title;
    private int credits;
    private UUID professorId; // может быть null
    private final Set<UUID> enrollmentIds = new LinkedHashSet<>();

    public Course(String title, int credits) {
        this.id = Ids.newId();
        setTitle(title);
        setCredits(credits);
    }

    Course(UUID id, String title, int credits, UUID professorId) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setTitle(title);
        setCredits(credits);
        this.professorId = professorId; // может быть null
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

    private void setCredits(int credits) {
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    public Optional<UUID> getProfessorId() {
        return Optional.ofNullable(professorId);
    }

    public void setProfessor(UUID professorId) {
        this.professorId = Objects.requireNonNull(professorId);
    }

    public void addEnrollment(UUID enrollmentId) {
        enrollmentIds.add(Objects.requireNonNull(enrollmentId));
    }

    public void removeEnrollment(UUID enrollmentId) {
        enrollmentIds.remove(enrollmentId);
    }

    public Set<UUID> getEnrollmentIds() {
        return Collections.unmodifiableSet(enrollmentIds);
    }

    @Override
    public String toString() {
        return "Course{" + id + ", '" + title + "', " + credits + " кр.}";
    }
}
