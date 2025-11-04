package university;

import java.util.*;

public class Enrollment {
    // DATA
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private int credits;
    private Grade grade; // может быть null до выставления

    // ACTION
    public Enrollment(UUID studentId, UUID courseId, int credits) {
        this.id = Ids.newId();
        setRefs(studentId, courseId);
        setCredits(credits);
    }

    // ACTION
    Enrollment(UUID id, UUID studentId, UUID courseId, int credits, Grade grade) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setRefs(studentId, courseId);
        setCredits(credits);
        this.grade = grade;
    }

    // CALCULATION
    public UUID getId() {
        return id;
    }

    // CALCULATION
    public UUID getStudentId() {
        return studentId;
    }

    // CALCULATION
    public UUID getCourseId() {
        return courseId;
    }

    // CALCULATION
    public int getCredits() {
        return credits;
    }

    // CALCULATION
    public Optional<Grade> getGrade() {
        return Optional.ofNullable(grade);
    }

    // ACTION
    public void setGrade(Grade g) {
        this.grade = Objects.requireNonNull(g);
    }

    // ACTION
    private void setRefs(UUID studentId, UUID courseId) {
        if (studentId == null || courseId == null) throw new IllegalArgumentException("ids");
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // ACTION
    private void setCredits(int credits) {
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    // CALCULATION
    @Override
    public String toString() {
        return "Enrollment{" + id + ", student=" + studentId + ", course=" + courseId +
                ", grade=" + (grade == null ? "-" : grade) + "}";
    }
}
