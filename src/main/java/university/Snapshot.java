package university;

import java.util.*;

public final class Snapshot {
    /**
     * All of these fields is DATA
     */
    public List<StudentSnap> students = new ArrayList<>();
    public List<ProfessorSnap> professors = new ArrayList<>();
    public List<CourseSnap> courses = new ArrayList<>();
    public List<EnrollmentSnap> enrollments = new ArrayList<>();

    public static final class StudentSnap {
        public UUID id;
        public String name;
        public List<UUID> enrollmentIds = new ArrayList<>();
    }

    public static final class ProfessorSnap {
        public UUID id;
        public String name;
        public List<UUID> courseIds = new ArrayList<>();
    }

    public static final class CourseSnap {
        public UUID id;
        public String title;
        public int credits;
        public UUID professorId; // может быть null
        public List<UUID> enrollmentIds = new ArrayList<>();
    }

    public static final class EnrollmentSnap {
        public UUID id;
        public UUID studentId;
        public UUID courseId;
        public int credits;
        public String grade; // "A"/"B"/... или null
    }
}
