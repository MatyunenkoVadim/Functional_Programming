package university;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Snapshot {
    /**
     * All of these fields is DATA
     */
    public List<StudentSnap> students = new ArrayList<>();
    public List<ProfessorSnap> professors = new ArrayList<>();
    public List<CourseSnap> courses = new ArrayList<>();
    public List<EnrollmentSnap> enrollments = new ArrayList<>();

    public Snapshot normalizedCopy() {
        return normalizedCopyOf(this);
    }

    public static Snapshot normalizedCopyOf(Snapshot in) {
        Snapshot src = (in == null) ? new Snapshot() : in;

        Snapshot out = new Snapshot();
        out.students = copyStudents(src.students);
        out.professors = copyProfessors(src.professors);
        out.courses = copyCourses(src.courses);
        out.enrollments = copyEnrollments(src.enrollments);
        return out;
    }

    private static List<StudentSnap> copyStudents(List<StudentSnap> src) {
        List<StudentSnap> list = (src == null) ? List.of() : src;
        List<StudentSnap> out = new ArrayList<>(list.size());
        for (StudentSnap s : list) {
            if (s == null) continue;
            StudentSnap c = new StudentSnap();
            c.id = s.id;
            c.name = s.name;
            c.enrollmentIds = (s.enrollmentIds == null) ? new ArrayList<>() : new ArrayList<>(s.enrollmentIds);
            out.add(c);
        }
        return out;
    }

    private static List<ProfessorSnap> copyProfessors(List<ProfessorSnap> src) {
        List<ProfessorSnap> list = (src == null) ? List.of() : src;
        List<ProfessorSnap> out = new ArrayList<>(list.size());
        for (ProfessorSnap p : list) {
            if (p == null) continue;
            ProfessorSnap c = new ProfessorSnap();
            c.id = p.id;
            c.name = p.name;
            c.courseIds = (p.courseIds == null) ? new ArrayList<>() : new ArrayList<>(p.courseIds);
            out.add(c);
        }
        return out;
    }

    private static List<CourseSnap> copyCourses(List<CourseSnap> src) {
        List<CourseSnap> list = (src == null) ? List.of() : src;
        List<CourseSnap> out = new ArrayList<>(list.size());
        for (CourseSnap crs : list) {
            if (crs == null) continue;
            CourseSnap c = new CourseSnap();
            c.id = crs.id;
            c.title = crs.title;
            c.credits = crs.credits;
            c.professorId = crs.professorId; // may be null
            c.enrollmentIds = (crs.enrollmentIds == null) ? new ArrayList<>() : new ArrayList<>(crs.enrollmentIds);
            out.add(c);
        }
        return out;
    }

    private static List<EnrollmentSnap> copyEnrollments(List<EnrollmentSnap> src) {
        List<EnrollmentSnap> list = (src == null) ? List.of() : src;
        List<EnrollmentSnap> out = new ArrayList<>(list.size());
        for (EnrollmentSnap e : list) {
            if (e == null) continue;
            EnrollmentSnap c = new EnrollmentSnap();
            c.id = e.id;
            c.studentId = e.studentId;
            c.courseId = e.courseId;
            c.credits = e.credits;
            c.grade = e.grade;
            out.add(c);
        }
        return out;
    }

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
        public UUID professorId;
        public List<UUID> enrollmentIds = new ArrayList<>();
    }

    public static final class EnrollmentSnap {
        public UUID id;
        public UUID studentId;
        public UUID courseId;
        public int credits;
        public String grade;
    }
}
