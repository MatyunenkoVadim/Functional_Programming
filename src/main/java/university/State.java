package university;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class State {
    private final Map<UUID, Student> students;
    private final Map<UUID, Professor> professors;
    private final Map<UUID, Course> courses;
    private final Map<UUID, Enrollment> enrollments;

    public State(Map<UUID, Student> students,
                 Map<UUID, Professor> professors,
                 Map<UUID, Course> courses,
                 Map<UUID, Enrollment> enrollments) {
        this.students = unmodifiableCopy(Objects.requireNonNull(students, "students"));
        this.professors = unmodifiableCopy(Objects.requireNonNull(professors, "professors"));
        this.courses = unmodifiableCopy(Objects.requireNonNull(courses, "courses"));
        this.enrollments = unmodifiableCopy(Objects.requireNonNull(enrollments, "enrollments"));
    }

    public static State empty() {
        return new State(new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public Map<UUID, Student> students() {
        return students;
    }

    public Map<UUID, Professor> professors() {
        return professors;
    }

    public Map<UUID, Course> courses() {
        return courses;
    }

    public Map<UUID, Enrollment> enrollments() {
        return enrollments;
    }

    public State withStudents(Map<UUID, Student> v) {
        return new State(v, this.professors, this.courses, this.enrollments);
    }

    public State withProfessors(Map<UUID, Professor> v) {
        return new State(this.students, v, this.courses, this.enrollments);
    }

    public State withCourses(Map<UUID, Course> v) {
        return new State(this.students, this.professors, v, this.enrollments);
    }

    public State withEnrollments(Map<UUID, Enrollment> v) {
        return new State(this.students, this.professors, this.courses, v);
    }

    private static <K, V> Map<K, V> unmodifiableCopy(Map<K, V> src) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(src));
    }
}
