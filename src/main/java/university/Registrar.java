package university;

import java.nio.file.Path;
import java.util.*;

public class Registrar {
    private State state = State.empty();

    // CRUD (ACTIONS)
    public Student addStudent(String name) {
        Student s = new Student(name);
        state = StateOps.putStudent(state, s);
        return s;
    }

    public Professor addProfessor(String name) {
        Professor p = new Professor(name);
        state = StateOps.putProfessor(state, p);
        return p;
    }

    public Course addCourse(String title, int credits) {
        Course c = new Course(title, credits);
        state = StateOps.putCourse(state, c);
        return c;
    }

    public void removeCourse(UUID courseId) {
        state = RegistrarCommands.removeCourse(state, courseId);
    }

    // Domain actions
    public void assignProfessor(UUID courseId, UUID profId) {
        state = RegistrarCommands.assignProfessor(state, courseId, profId);
    }

    public Enrollment enroll(UUID studentId, UUID courseId) {
        RegistrarCommands.Result<Enrollment> r = RegistrarCommands.enroll(state, studentId, courseId);
        state = r.state();
        return r.value();
    }

    public void drop(UUID studentId, UUID courseId) {
        state = RegistrarCommands.drop(state, studentId, courseId);
    }

    public void grade(UUID studentId, UUID courseId, Grade grade) {
        state = RegistrarCommands.grade(state, studentId, courseId, grade);
    }

    // Output
    public QueryResults.Transcript getTranscript(UUID studentId) {
        Student s = getStudent(studentId);

        var studentEnrollments = Calculations.enrollmentsOfStudent(s, state.enrollments());

        if (studentEnrollments.isEmpty()) {
            return new QueryResults.Transcript(s, List.of(), 0.0);
        }

        List<String> lines = Calculations.transcriptLines(studentEnrollments, state.courses());
        double gpa = Calculations.calculateGpa(studentEnrollments, state.courses());

        return new QueryResults.Transcript(s, lines, gpa);
    }

    public QueryResults.Roster getRoster(UUID courseId) {
        Course c = getCourse(courseId);

        Professor prof = c.getProfessorId()
                .map(pid -> state.professors().get(pid))
                .orElse(null);

        List<Student> roster = Calculations.rosterForCourse(courseId, state.courses(), state.enrollments(), state.students());

        return new QueryResults.Roster(c, prof, roster);
    }

    public QueryResults.ProfessorCourses getProfessorCourses(UUID profId) {
        Professor p = getProfessor(profId);

        List<Course> list = Calculations.coursesOfProfessor(p, state.courses());

        return new QueryResults.ProfessorCourses(p, list);
    }

    public QueryResults.SearchResult search(String query) {
        List<Student> st = Calculations.searchStudents(query, state.students().values());
        List<Course>  cs = Calculations.searchCourses(query, state.courses().values());
        return new QueryResults.SearchResult(query, st, cs);
    }

    // Search methods
    private Optional<Enrollment> findEnrollment(UUID studentId, UUID courseId) {
        return state.enrollments().values().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst();
    }

    private Student getStudent(UUID id) {
        var s = state.students().get(id);
        if (s == null) throw new IllegalArgumentException("student not found");
        return s;
    }

    private Professor getProfessor(UUID id) {
        var p = state.professors().get(id);
        if (p == null) throw new IllegalArgumentException("professor not found");
        return p;
    }

    private Course getCourse(UUID id) {
        var c = state.courses().get(id);
        if (c == null) throw new IllegalArgumentException("course not found");
        return c;
    }

    private static <K, V> LinkedHashMap<K, V> copyMap(Map<K, V> src) {
        return new LinkedHashMap<>(src);
    }

    // Save / Load
    public void saveToJson(Path path) {
        Snapshot snap = toSnapshot(state);
        new JsonStore().save(path, snap);
    }

    public void loadFromJson(Path path) {
        Snapshot snap = new JsonStore().load(path);
        snap = SnapshotNormalizer.normalize(snap);

        Map<UUID, Student> students = new LinkedHashMap<>();
        Map<UUID, Professor> professors = new LinkedHashMap<>();
        Map<UUID, Course> courses = new LinkedHashMap<>();
        Map<UUID, Enrollment> enrollments = new LinkedHashMap<>();

        for (var ss : snap.students) {
            var s = new Student(ss.id, ss.name, ss.enrollmentIds);
            students.put(s.getId(), s);
        }

        for (var ps : snap.professors) {
            var p = new Professor(ps.id, ps.name, ps.courseIds);
            professors.put(p.getId(), p);
        }

        for (var cs : snap.courses) {
            var c = new Course(cs.id, cs.title, cs.credits, cs.professorId, cs.enrollmentIds);
            courses.put(c.getId(), c);
        }

        for (var es : snap.enrollments) {
            Grade g = null;
            if (es.grade != null) {
                try {
                    g = Grade.valueOf(es.grade);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid grade in JSON for enrollment " + es.id + ": " + es.grade);
                }
            }
            var e = new Enrollment(es.id, es.studentId, es.courseId, es.credits, g);
            enrollments.put(e.getId(), e);
        }

        state = new State(students, professors, courses, enrollments);
    }

    private static Snapshot toSnapshot(State st) {
        Snapshot snap = new Snapshot();

        for (var s : st.students().values()) {
            var ss = new Snapshot.StudentSnap();
            ss.id = s.getId();
            ss.name = s.getName();
            ss.enrollmentIds = new ArrayList<>(s.getEnrollmentIds());
            snap.students.add(ss);
        }

        for (var p : st.professors().values()) {
            var ps = new Snapshot.ProfessorSnap();
            ps.id = p.getId();
            ps.name = p.getName();
            ps.courseIds = new ArrayList<>(p.getCourseIds());
            snap.professors.add(ps);
        }

        for (var c : st.courses().values()) {
            var cs = new Snapshot.CourseSnap();
            cs.id = c.getId();
            cs.title = c.getTitle();
            cs.credits = c.getCredits();
            cs.professorId = c.getProfessorId().orElse(null);
            cs.enrollmentIds = new ArrayList<>(c.getEnrollmentIds());
            snap.courses.add(cs);
        }

        for (var e : st.enrollments().values()) {
            var es = new Snapshot.EnrollmentSnap();
            es.id = e.getId();
            es.studentId = e.getStudentId();
            es.courseId = e.getCourseId();
            es.credits = e.getCredits();
            es.grade = e.getGrade().map(Enum::name).orElse(null);
            snap.enrollments.add(es);
        }

        return snap;
    }
}
