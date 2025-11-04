package university;

import java.util.*;

public class Course {
    // DATA
    private UUID id;
    private String title;
    private int credits;
    private UUID professorId; // может быть null
    private final Set<UUID> enrollmentIds = new LinkedHashSet<>();

    // ACTION
    public Course(String title, int credits) {
        this.id = Ids.newId();
        setTitle(title);
        setCredits(credits);
    }

    // ACTION
    Course(UUID id, String title, int credits, UUID professorId) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setTitle(title);
        setCredits(credits);
        this.professorId = professorId; // может быть null
    }

    // CALCULATION
    public UUID getId() {
        return id;
    }

    // CALCULATION
    public String getTitle() {
        return title;
    }

    // CALCULATION
    public int getCredits() {
        return credits;
    }

    // ACTION
    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title");
        this.title = title.trim();
    }

    // ACTION
    private void setCredits(int credits) {
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    // ACTION
    public Optional<UUID> getProfessorId() {
        return Optional.ofNullable(professorId);
    }

    // ACTION
    public void setProfessor(UUID professorId) {
        this.professorId = Objects.requireNonNull(professorId);
    }

    // ACTION
    public void addEnrollment(UUID enrollmentId) {
        enrollmentIds.add(Objects.requireNonNull(enrollmentId));
    }

    // ACTION
    public void removeEnrollment(UUID enrollmentId) {
        enrollmentIds.remove(enrollmentId);
    }

    // ACTION
    public Set<UUID> getEnrollmentIds() {
        return Collections.unmodifiableSet(enrollmentIds);
    }

    // CALCULATION
    @Override
    public String toString() {
        return "Course{" + id + ", '" + title + "', " + credits + " кр.}";
    }
}
