package university;

import java.util.*;

public final class Enrollment {
    private final UUID id;
    private final UUID studentId;
    private final UUID courseId;
    private final int credits;
    private final Grade grade; // может быть null до выставления

    public Enrollment(UUID studentId, UUID courseId, int credits) {
        this(Ids.newId(), studentId, courseId, credits, null);
    }

    Enrollment(UUID id, UUID studentId, UUID courseId, int credits, Grade grade) {
        if (id == null) throw new IllegalArgumentException("id");
        if (studentId == null || courseId == null) throw new IllegalArgumentException("ids");
        if (credits <= 0) throw new IllegalArgumentException("credits");

        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.credits = credits;
        this.grade = grade; // may be null
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

    public Enrollment withGrade(Grade g) {
        return new Enrollment(this.id, this.studentId, this.courseId, this.credits, Objects.requireNonNull(g, "grade"));
    }

    public Enrollment withoutGrade() {
        return new Enrollment(this.id, this.studentId, this.courseId, this.credits, null);
    }

    @Override
    public String toString() {
        return "Enrollment{" + id + ", student=" + studentId + ", course=" + courseId +
                ", grade=" + (grade == null ? "-" : grade) + "}";
    }
}
