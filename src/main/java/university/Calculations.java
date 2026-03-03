package university;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Calculations {
    private Calculations() {
    }

    // Расчёт GPA по списку зачислений и карте курсов (веса берём из credits)
    public static double calculateGpa(List<Enrollment> enrollments, Map<UUID, Course> courseById) {
        int totalCredits = 0;
        double totalPoints = 0.0;

        for (Enrollment e : enrollments) {
            Course c = courseById.get(e.getCourseId());
            if (c == null) continue;
            if (e.getGrade().isPresent()) {
                int cr = c.getCredits();
                totalCredits += cr;
                totalPoints += cr * e.getGrade().get().points();
            }
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    // Формирует строки транскрипта: " - Title (N кр.) — GradeOrDash"
    public static List<String> transcriptLines(List<Enrollment> enrollments, Map<UUID, Course> courseById) {
        List<String> lines = new ArrayList<>();
        for (Enrollment e : enrollments) {
            Course c = courseById.get(e.getCourseId());
            if (c == null) continue;
            String grade = e.getGrade().map(Enum::name).orElse("-");
            lines.add(String.format(" - %s (%d кр.) — %s", c.getTitle(), c.getCredits(), grade));
        }
        return lines;
    }

    // Возвращает список студентов, записанных на курс
    public static List<Student> rosterForCourse(
            UUID courseId,
            Map<UUID, Course> courseById,
            Map<UUID, Enrollment> enrollmentById,
            Map<UUID, Student> studentById
    ) {
        Course c = courseById.get(courseId);
        if (c == null) return List.of();

        List<Student> result = new ArrayList<>();
        for (UUID enrId : c.getEnrollmentIds()) {
            Enrollment e = enrollmentById.get(enrId);
            if (e == null) continue;
            Student s = studentById.get(e.getStudentId());
            if (s != null) result.add(s);
        }
        return result;
    }

    // Фильтрация зачислений конкретного студента
    public static List<Enrollment> enrollmentsOfStudent(Student s, StateView st) {
        return resolveAll(s.getEnrollmentIds(), id -> st.enrollment(id).orElse(null));
    }

    // Список курсов преподавателя
    public static List<Course> coursesOfProfessor(Professor professor, StateView st) {
        return resolveAll(professor.getCourseIds(), id -> st.course(id).orElse(null));
    }

    // Поиск студентов по подстроке (без учёта регистра)
    public static List<Student> searchStudents(String query, Collection<Student> students) {
        return searchBy(query, students, Student::getName);
    }

    // Поиск курсов по подстроке (без учёта регистра)
    public static List<Course> searchCourses(String query, Collection<Course> courses) {
        return searchBy(query, courses, Course::getTitle);
    }

    public static double calculateGpa(List<Enrollment> enrollments, StateView st) {
        int totalCredits = 0;
        double totalPoints = 0.0;

        for (Enrollment e : enrollments) {
            Course c = st.course(e.getCourseId()).orElse(null);
            if (c == null) continue;

            if (e.getGrade().isPresent()) {
                int cr = c.getCredits();
                totalCredits += cr;
                totalPoints += cr * e.getGrade().get().points();
            }
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }


    public static List<String> transcriptLines(List<Enrollment> enrollments, StateView st) {
        List<String> lines = new ArrayList<>();
        for (Enrollment e : enrollments) {
            Course c = st.course(e.getCourseId()).orElse(null);
            if (c == null) continue;

            String grade = e.getGrade().map(Enum::name).orElse("-");
            lines.add(String.format(" - %s (%d кр.) — %s", c.getTitle(), c.getCredits(), grade));
        }
        return lines;
    }

    public static List<Student> rosterForCourse(UUID courseId, StateView st) {
        Course c = st.course(courseId).orElse(null);
        if (c == null) return List.of();

        List<Enrollment> enrollments = resolveAll(c.getEnrollmentIds(), id -> st.enrollment(id).orElse(null));
        List<Student> out = new ArrayList<>(enrollments.size());

        for (Enrollment e : enrollments) {
            Student s = st.student(e.getStudentId()).orElse(null);
            if (s != null) out.add(s);
        }
        return out;
    }

    private static Map<UUID, Course> indexCourses(Collection<Course> courses) {
        LinkedHashMap<UUID, Course> m = new LinkedHashMap<>();
        for (Course c : courses) m.put(c.getId(), c);
        return m;
    }

    private static Map<UUID, Enrollment> indexEnrollments(Collection<Enrollment> enrollments) {
        LinkedHashMap<UUID, Enrollment> m = new LinkedHashMap<>();
        for (Enrollment e : enrollments) m.put(e.getId(), e);
        return m;
    }

    private static Map<UUID, Student> indexStudents(Collection<Student> students) {
        LinkedHashMap<UUID, Student> m = new LinkedHashMap<>();
        for (Student s : students) m.put(s.getId(), s);
        return m;
    }

    public static <T> List<T> searchBy(String query, Collection<T> items, Function<T, String> text) {
        String q = (query == null) ? "" : query.toLowerCase();
        List<T> out = new ArrayList<>();
        for (T it : items) {
            if (it == null) continue;
            String s = text.apply(it);
            if (s != null && s.toLowerCase().contains(q)) out.add(it);
        }
        return out;
    }

    public static <T> List<T> resolveAll(Collection<UUID> ids, Function<UUID, T> resolver) {
        Collection<UUID> src = (ids == null) ? List.of() : ids;
        List<T> out = new ArrayList<>(src.size());
        for (UUID id : src) {
            if (id == null) continue;
            T v = resolver.apply(id);
            if (v != null) out.add(v);
        }
        return out;
    }
}
