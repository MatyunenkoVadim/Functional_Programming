package university;

import java.util.*;

public final class State implements StateView {
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

    @Override
    public Optional<Student> student(UUID id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public Optional<Professor> professor(UUID id) {
        return Optional.ofNullable(professors.get(id));
    }

    @Override
    public Optional<Course> course(UUID id) {
        return Optional.ofNullable(courses.get(id));
    }

    @Override
    public Optional<Enrollment> enrollment(UUID id) {
        return Optional.ofNullable(enrollments.get(id));
    }

    @Override
    public Collection<Student> students() {
        return students.values();
    }

    @Override
    public Collection<Professor> professors() {
        return professors.values();
    }

    @Override
    public Collection<Course> courses() {
        return courses.values();
    }

    @Override
    public Collection<Enrollment> enrollments() {
        return enrollments.values();
    }

    Map<UUID, Student> studentsMap() { return students; }
    Map<UUID, Professor> professorsMap() { return professors; }
    Map<UUID, Course> coursesMap() { return courses; }
    Map<UUID, Enrollment> enrollmentsMap() { return enrollments; }

    State withStudents(Map<UUID, Student> v) {
        return new State(v, this.professors, this.courses, this.enrollments);
    }

    State withProfessors(Map<UUID, Professor> v) {
        return new State(this.students, v, this.courses, this.enrollments);
    }

    State withCourses(Map<UUID, Course> v) {
        return new State(this.students, this.professors, v, this.enrollments);
    }

    State withEnrollments(Map<UUID, Enrollment> v) {
        return new State(this.students, this.professors, this.courses, v);
    }

    private static <K, V> Map<K, V> unmodifiableCopy(Map<K, V> src) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(src));
    }
}