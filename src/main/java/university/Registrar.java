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

        var studentEnrollments = Calculations.enrollmentsOfStudent(s, state);

        if (studentEnrollments.isEmpty()) {
            return new QueryResults.Transcript(s, List.of(), 0.0);
        }

        List<String> lines = Calculations.transcriptLines(studentEnrollments, state);
        double gpa = Calculations.calculateGpa(studentEnrollments, state);

        return new QueryResults.Transcript(s, lines, gpa);
    }

    public QueryResults.Roster getRoster(UUID courseId) {
        Course c = getCourse(courseId);

        Professor prof = c.getProfessorId()
                .flatMap(state::professor)
                .orElse(null);

        List<Student> roster = Calculations.rosterForCourse(courseId, state);

        return new QueryResults.Roster(c, prof, roster);
    }

    public QueryResults.ProfessorCourses getProfessorCourses(UUID profId) {
        Professor p = getProfessor(profId);

        List<Course> list = Calculations.coursesOfProfessor(p, state);

        return new QueryResults.ProfessorCourses(p, list);
    }

    public QueryResults.SearchResult search(String query) {
        List<Student> st = Calculations.searchStudents(query, state.students());
        List<Course>  cs = Calculations.searchCourses(query, state.courses());
        return new QueryResults.SearchResult(query, st, cs);
    }

    // Search methods
    private Student getStudent(UUID id) {
        return state.student(id).orElseThrow(() -> new IllegalArgumentException("student not found"));
    }

    private Professor getProfessor(UUID id) {
        return state.professor(id).orElseThrow(() -> new IllegalArgumentException("professor not found"));
    }

    private Course getCourse(UUID id) {
        return state.course(id).orElseThrow(() -> new IllegalArgumentException("course not found"));
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

        for (var s : st.studentsMap().values()) {
            var ss = new Snapshot.StudentSnap();
            ss.id = s.getId();
            ss.name = s.getName();
            ss.enrollmentIds = new ArrayList<>(s.getEnrollmentIds());
            snap.students.add(ss);
        }

        for (var p : st.professorsMap().values()) {
            var ps = new Snapshot.ProfessorSnap();
            ps.id = p.getId();
            ps.name = p.getName();
            ps.courseIds = new ArrayList<>(p.getCourseIds());
            snap.professors.add(ps);
        }

        for (var c : st.coursesMap().values()) {
            var cs = new Snapshot.CourseSnap();
            cs.id = c.getId();
            cs.title = c.getTitle();
            cs.credits = c.getCredits();
            cs.professorId = c.getProfessorId().orElse(null);
            cs.enrollmentIds = new ArrayList<>(c.getEnrollmentIds());
            snap.courses.add(cs);
        }

        for (var e : st.enrollmentsMap().values()) {
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
