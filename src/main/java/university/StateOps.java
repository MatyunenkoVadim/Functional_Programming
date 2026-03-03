package university;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Низкоуровневые операции над State
 */
public final class StateOps {
    private StateOps() {
    }

    public static State putStudent(State st, Student s) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(s, "student");
        Map<UUID, Student> students = copy(st.students());
        students.put(s.getId(), s);
        return st.withStudents(students);
    }

    public static State putProfessor(State st, Professor p) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(p, "professor");
        Map<UUID, Professor> professors = copy(st.professors());
        professors.put(p.getId(), p);
        return st.withProfessors(professors);
    }

    public static State putCourse(State st, Course c) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(c, "course");
        Map<UUID, Course> courses = copy(st.courses());
        courses.put(c.getId(), c);
        return st.withCourses(courses);
    }

    public static State putEnrollment(State st, Enrollment e) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(e, "enrollment");
        Map<UUID, Enrollment> enrollments = copy(st.enrollments());
        enrollments.put(e.getId(), e);
        return st.withEnrollments(enrollments);
    }

    public static State updateStudent(State st, UUID studentId, UnaryOperator<Student> f) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(studentId, "studentId");
        Objects.requireNonNull(f, "mapper");

        Student cur = st.students().get(studentId);
        if (cur == null) throw new IllegalArgumentException("student not found");

        Student updated = Objects.requireNonNull(f.apply(cur), "updated student");
        return putStudent(st, updated);
    }

    public static State updateProfessor(State st, UUID professorId, UnaryOperator<Professor> f) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(professorId, "professorId");
        Objects.requireNonNull(f, "mapper");

        Professor cur = st.professors().get(professorId);
        if (cur == null) throw new IllegalArgumentException("professor not found");

        Professor updated = Objects.requireNonNull(f.apply(cur), "updated professor");
        return putProfessor(st, updated);
    }

    public static State updateCourse(State st, UUID courseId, UnaryOperator<Course> f) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(courseId, "courseId");
        Objects.requireNonNull(f, "mapper");

        Course cur = st.courses().get(courseId);
        if (cur == null) throw new IllegalArgumentException("course not found");

        Course updated = Objects.requireNonNull(f.apply(cur), "updated course");
        return putCourse(st, updated);
    }

    public static State updateEnrollment(State st, UUID enrollmentId, UnaryOperator<Enrollment> f) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(enrollmentId, "enrollmentId");
        Objects.requireNonNull(f, "mapper");

        Enrollment cur = st.enrollments().get(enrollmentId);
        if (cur == null) throw new IllegalArgumentException("enrollment not found");

        Enrollment updated = Objects.requireNonNull(f.apply(cur), "updated enrollment");
        return putEnrollment(st, updated);
    }

    public static State removeEnrollment(State st, UUID enrollmentId) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(enrollmentId, "enrollmentId");

        Map<UUID, Enrollment> enrollments = copy(st.enrollments());
        enrollments.remove(enrollmentId);
        return st.withEnrollments(enrollments);
    }

    public static State removeCourse(State st, UUID courseId) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(courseId, "courseId");

        Map<UUID, Course> courses = copy(st.courses());
        courses.remove(courseId);
        return st.withCourses(courses);
    }

    private static <K, V> LinkedHashMap<K, V> copy(Map<K, V> src) {
        return new LinkedHashMap<>(src);
    }
}
