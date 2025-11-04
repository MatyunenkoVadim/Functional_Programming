package university;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CALCULATIONS
 */
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
    public static List<Enrollment> enrollmentsOfStudent(Student s, Map<UUID, Enrollment> enrollmentById) {
        return s.getEnrollmentIds().stream()
                .map(enrollmentById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Список курсов преподавателя
    public static List<Course> coursesOfProfessor(
            Professor professor,
            Map<UUID, Course> courseById
    ) {
        List<Course> result = new ArrayList<>();
        for (UUID cid : professor.getCourseIds()) {
            Course c = courseById.get(cid);
            if (c != null) result.add(c);
        }
        return result;
    }

    // Поиск студентов по подстроке (без учёта регистра)
    public static List<Student> searchStudents(String query, Collection<Student> students) {
        String q = query.toLowerCase();
        List<Student> out = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(q)) out.add(s);
        }
        return out;
    }

    // Поиск курсов по подстроке (без учёта регистра)
    public static List<Course> searchCourses(String query, Collection<Course> courses) {
        String q = query.toLowerCase();
        List<Course> out = new ArrayList<>();
        for (Course c : courses) {
            if (c.getTitle().toLowerCase().contains(q)) out.add(c);
        }
        return out;
    }
}
