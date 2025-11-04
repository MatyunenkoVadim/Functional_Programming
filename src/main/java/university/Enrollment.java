package university;

import java.util.*;

public class Enrollment {
    private final UUID id = Ids.newId();
    private final UUID studentId;
    private final UUID courseId;
    private final int credits;
    private Grade grade; // может быть null до выставления

    public Enrollment(UUID studentId, UUID courseId, int credits) {
        this.studentId = Objects.requireNonNull(studentId);
        this.courseId = Objects.requireNonNull(courseId);
        if (credits <= 0) throw new IllegalArgumentException("credits");
        this.credits = credits;
    }

    public UUID getId(){ return id; }
    public UUID getStudentId(){ return studentId; }
    public UUID getCourseId(){ return courseId; }
    public int getCredits(){ return credits; }
    public Optional<Grade> getGrade(){ return Optional.ofNullable(grade); }
    public void setGrade(Grade g){ this.grade = Objects.requireNonNull(g); }

    @Override public String toString(){
        return "Enrollment{" + id + ", student=" + studentId + ", course=" + courseId +
                ", grade=" + (grade == null ? "-" : grade) + "}";
    }
}
