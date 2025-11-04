package university;

import java.util.*;

public class Course {
    private final UUID id = Ids.newId();
    private String title;
    private final int credits;
    private UUID professorId; // может быть null
    private final Set<UUID> enrollmentIds = new LinkedHashSet<>();

    public Course(String title, int credits) {
        setTitle(title);
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    public UUID getId(){ return id; }
    public String getTitle(){ return title; }
    public int getCredits(){ return credits; }

    public void setTitle(String title){
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title");
        this.title = title.trim();
    }

    public Optional<UUID> getProfessorId(){ return Optional.ofNullable(professorId); }
    public void setProfessor(UUID professorId){ this.professorId = Objects.requireNonNull(professorId); }

    public void addEnrollment(UUID enrollmentId){ enrollmentIds.add(Objects.requireNonNull(enrollmentId)); }
    public void removeEnrollment(UUID enrollmentId){ enrollmentIds.remove(enrollmentId); }
    public Set<UUID> getEnrollmentIds(){ return Collections.unmodifiableSet(enrollmentIds); }

    @Override public String toString(){ return "Course{" + id + ", '" + title + "', " + credits + " кр.}"; }
}
