package university;

import java.util.*;

public class Enrollment {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private int credits;
    private Grade grade; // может быть null до выставления

    public Enrollment(UUID studentId, UUID courseId, int credits) {
        this.id = Ids.newId();
        setRefs(studentId, courseId);
        setCredits(credits);
    }

    Enrollment(UUID id, UUID studentId, UUID courseId, int credits, Grade grade) {
        if (id == null) throw new IllegalArgumentException("id");
        this.id = id;
        setRefs(studentId, courseId);
        setCredits(credits);
        this.grade = grade;
    }

    public UUID getId() {
        return id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public int getCredits() {
        return credits;
    }

    public Optional<Grade> getGrade() {
        return Optional.ofNullable(grade);
    }

    public void setGrade(Grade g) {
        this.grade = Objects.requireNonNull(g);
    }

    private void setRefs(UUID studentId, UUID courseId) {
        if (studentId == null || courseId == null) throw new IllegalArgumentException("ids");
        this.studentId = studentId;
        this.courseId = courseId;
    }

    private void setCredits(int credits) {
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Enrollment{" + id + ", student=" + studentId + ", course=" + courseId +
                ", grade=" + (grade == null ? "-" : grade) + "}";
    }
}
