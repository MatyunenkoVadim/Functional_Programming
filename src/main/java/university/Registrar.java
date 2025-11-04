package university;

import java.util.*;

public class Registrar {
    private final Map<UUID, Student> students = new LinkedHashMap<>();
    private final Map<UUID, Professor> professors = new LinkedHashMap<>();
    private final Map<UUID, Course> courses = new LinkedHashMap<>();
    private final Map<UUID, Enrollment> enrollments = new LinkedHashMap<>();

    public Student addStudent(String name) {
        Student s = new Student(name);
        students.put(s.getId(), s);
        return s;
    }

    public Professor addProfessor(String name) {
        Professor p = new Professor(name);
        professors.put(p.getId(), p);
        return p;
    }

    public Course addCourse(String title, int credits) {
        Course c = new Course(title, credits);
        courses.put(c.getId(), c);
        return c;
    }

    public void removeCourse(UUID courseId) {
        Course c = getCourse(courseId);
        // снять курс у преподавателя, если был назначен
        c.getProfessorId().ifPresent(pid -> professors.get(pid).unassignCourse(courseId));
        // удалить зачисления
        for (UUID enrId : new ArrayList<>(c.getEnrollmentIds())) {
            Enrollment e = enrollments.remove(enrId);
            students.get(e.getStudentId()).removeEnrollment(enrId);
            c.removeEnrollment(enrId);
        }
        courses.remove(courseId);
    }

    // Связи

    public void assignProfessor(UUID courseId, UUID profId) {
        Course c = getCourse(courseId);
        Professor p = getProfessor(profId);
        c.setProfessor(p.getId());
        p.assignCourse(c.getId());
    }

    public Enrollment enroll(UUID studentId, UUID courseId) {
        Student s = getStudent(studentId);
        Course c = getCourse(courseId);

        // простая защита от дубля
        if (findEnrollment(studentId, courseId).isPresent())
            throw new IllegalStateException("Студент уже записан на этот курс");

        Enrollment e = new Enrollment(studentId, courseId, c.getCredits());
        enrollments.put(e.getId(), e);

        s.addEnrollment(e.getId());
        c.addEnrollment(e.getId());
        return e;
    }

    public void drop(UUID studentId, UUID courseId) {
        Enrollment e = findEnrollment(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        Student s = getStudent(studentId);
        Course c = getCourse(courseId);

        s.removeEnrollment(e.getId());
        c.removeEnrollment(e.getId());
        enrollments.remove(e.getId());
    }

    public void grade(UUID studentId, UUID courseId, Grade grade) {
        Enrollment e = findEnrollment(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        e.setGrade(grade);
    }

    // Отчёты/вывод

    public void printTranscript(UUID studentId) {
        Student s = getStudent(studentId);
        System.out.println("Студент: " + s);

        var studentEnrollments = s.getEnrollmentIds().stream()
                .map(enrollments::get).toList();

        if (studentEnrollments.isEmpty()) {
            System.out.println("Курсов нет.");
            return;
        }

        int totalCredits = 0;
        double totalPoints = 0.0;

        for (Enrollment e : studentEnrollments) {
            Course c = courses.get(e.getCourseId());
            String grade = e.getGrade().map(Enum::name).orElse("-");
            System.out.printf(" - %s (%d кр.) — %s%n", c.getTitle(), c.getCredits(), grade);

            if (e.getGrade().isPresent()) {
                totalCredits += c.getCredits();
                totalPoints  += c.getCredits() * e.getGrade().get().points();
            }
        }

        double gpa = totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
        System.out.printf("GPA: %.2f%n", gpa);
    }

    public void printRoster(UUID courseId) {
        Course c = getCourse(courseId);
        System.out.println("Курс: " + c);
        c.getProfessorId().ifPresent(pid -> System.out.println("Преподаватель: " + professors.get(pid)));
        if (c.getEnrollmentIds().isEmpty()) { System.out.println("Группа пуста."); return; }
        for (UUID enrId : c.getEnrollmentIds()) {
            Enrollment e = enrollments.get(enrId);
            System.out.println(" - " + students.get(e.getStudentId()));
        }
    }

    public void printProfessorCourses(UUID profId) {
        Professor p = getProfessor(profId);
        System.out.println("Преподаватель: " + p);
        if (p.getCourseIds().isEmpty()) { System.out.println("Курсов нет."); return; }
        for (UUID cid : p.getCourseIds()) System.out.println(" - " + courses.get(cid));
    }

    public void search(String query) {
        String q = query.toLowerCase();
        System.out.println("Студенты:");
        students.values().stream().filter(s -> s.getName().toLowerCase().contains(q)).forEach(s -> System.out.println(" - " + s));
        System.out.println("Курсы:");
        courses.values().stream().filter(c -> c.getTitle().toLowerCase().contains(q)).forEach(c -> System.out.println(" - " + c));
    }

    // Вспомогательные

    private Optional<Enrollment> findEnrollment(UUID studentId, UUID courseId) {
        return enrollments.values().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst();
    }

    private Student   getStudent(UUID id){ var s = students.get(id);   if (s==null) throw new IllegalArgumentException("student not found");   return s; }
    private Professor getProfessor(UUID id){ var p = professors.get(id); if (p==null) throw new IllegalArgumentException("professor not found"); return p; }
    private Course    getCourse(UUID id){ var c = courses.get(id);    if (c==null) throw new IllegalArgumentException("course not found");    return c; }
}
