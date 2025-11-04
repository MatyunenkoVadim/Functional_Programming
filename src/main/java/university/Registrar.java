package university;

import java.util.*;
import java.nio.file.Path;

public class Registrar {
    /**
     * All of these fields is DATA
     */
    private final Map<UUID, Student> students = new LinkedHashMap<>();
    private final Map<UUID, Professor> professors = new LinkedHashMap<>();
    private final Map<UUID, Course> courses = new LinkedHashMap<>();
    private final Map<UUID, Enrollment> enrollments = new LinkedHashMap<>();


    public Student addStudent(String name) {                                        // ACTION
        Student s = new Student(name);
        students.put(s.getId(), s);
        return s;
    }

    public Professor addProfessor(String name) {                                    // ACTION
        Professor p = new Professor(name);
        professors.put(p.getId(), p);
        return p;
    }

    public Course addCourse(String title, int credits) {                            // ACTION
        Course c = new Course(title, credits);
        courses.put(c.getId(), c);
        return c;
    }

    public void removeCourse(UUID courseId) {                                       // ACTION
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

    public void assignProfessor(UUID courseId, UUID profId) {                       // ACTION
        Course c = getCourse(courseId);
        Professor p = getProfessor(profId);
        c.setProfessor(p.getId());
        p.assignCourse(c.getId());
    }

    public Enrollment enroll(UUID studentId, UUID courseId) {                       // ACTION
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

    public void drop(UUID studentId, UUID courseId) {                               // ACTION
        Enrollment e = findEnrollment(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        Student s = getStudent(studentId);
        Course c = getCourse(courseId);

        s.removeEnrollment(e.getId());
        c.removeEnrollment(e.getId());
        enrollments.remove(e.getId());
    }

    public void grade(UUID studentId, UUID courseId, Grade grade) {                 // ACTION
        Enrollment e = findEnrollment(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        e.setGrade(grade);
    }

    // Отчёты/вывод

    public void printTranscript(UUID studentId) {                                   // ACTION + CALCULATION
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
                totalPoints += c.getCredits() * e.getGrade().get().points();
            }
        }

        double gpa = totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
        System.out.printf("GPA: %.2f%n", gpa);
    }

    public void printRoster(UUID courseId) {                                        // ACTION + CALCULATION
        Course c = getCourse(courseId);
        System.out.println("Курс: " + c);
        c.getProfessorId().ifPresent(pid -> System.out.println("Преподаватель: " + professors.get(pid)));
        if (c.getEnrollmentIds().isEmpty()) {
            System.out.println("Группа пуста.");
            return;
        }
        for (UUID enrId : c.getEnrollmentIds()) {
            Enrollment e = enrollments.get(enrId);
            System.out.println(" - " + students.get(e.getStudentId()));
        }
    }

    public void printProfessorCourses(UUID profId) {                                // ACTION
        Professor p = getProfessor(profId);
        System.out.println("Преподаватель: " + p);
        if (p.getCourseIds().isEmpty()) {
            System.out.println("Курсов нет.");
            return;
        }
        for (UUID cid : p.getCourseIds()) System.out.println(" - " + courses.get(cid));
    }

    public void search(String query) {                                              // ACTION
        String q = query.toLowerCase();
        System.out.println("Студенты:");
        students.values().stream().filter(s -> s.getName().toLowerCase().contains(q)).forEach(s -> System.out.println(" - " + s));
        System.out.println("Курсы:");
        courses.values().stream().filter(c -> c.getTitle().toLowerCase().contains(q)).forEach(c -> System.out.println(" - " + c));
    }

    // Вспомогательные

    private Optional<Enrollment> findEnrollment(UUID studentId, UUID courseId) {    // CALCULATION
        return enrollments.values().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst();
    }

    private Student getStudent(UUID id) {                                           // CALCULATION
        var s = students.get(id);
        if (s == null) throw new IllegalArgumentException("student not found");
        return s;
    }

    private Professor getProfessor(UUID id) {                                       // CALCULATION
        var p = professors.get(id);
        if (p == null) throw new IllegalArgumentException("professor not found");
        return p;
    }

    private Course getCourse(UUID id) {                                             // CALCULATION
        var c = courses.get(id);
        if (c == null) throw new IllegalArgumentException("course not found");
        return c;
    }

    //Save and Load
    public void saveToJson(Path path) {                                             // ACTION
        Snapshot snap = new Snapshot();

        // students
        for (var s : students.values()) {
            var ss = new Snapshot.StudentSnap();
            ss.id = s.getId();
            ss.name = s.getName();
            ss.enrollmentIds = new ArrayList<>(s.getEnrollmentIds());
            snap.students.add(ss);
        }

        // professors
        for (var p : professors.values()) {
            var ps = new Snapshot.ProfessorSnap();
            ps.id = p.getId();
            ps.name = p.getName();
            ps.courseIds = new ArrayList<>(p.getCourseIds());
            snap.professors.add(ps);
        }

        // courses
        for (var c : courses.values()) {
            var cs = new Snapshot.CourseSnap();
            cs.id = c.getId();
            cs.title = c.getTitle();
            cs.credits = c.getCredits();
            cs.professorId = c.getProfessorId().orElse(null);
            cs.enrollmentIds = new ArrayList<>(c.getEnrollmentIds());
            snap.courses.add(cs);
        }

        // enrollments
        for (var e : enrollments.values()) {
            var es = new Snapshot.EnrollmentSnap();
            es.id = e.getId();
            es.studentId = e.getStudentId();
            es.courseId = e.getCourseId();
            es.credits = e.getCredits();
            es.grade = e.getGrade().map(Enum::name).orElse(null);
            snap.enrollments.add(es);
        }

        new JsonStore().save(path, snap);
    }

    public void loadFromJson(Path path) {                                           // ACTION
        Snapshot snap = new JsonStore().load(path);

        // очистим текущее состояние
        students.clear();
        professors.clear();
        courses.clear();
        enrollments.clear();

        // восстановление студентов
        for (var ss : snap.students) {
            var s = new Student(ss.id, ss.name);
            // enrollments добавим позже, когда они будут загружены
            students.put(s.getId(), s);
        }

        // преподаватели
        for (var ps : snap.professors) {
            var p = new Professor(ps.id, ps.name);
            professors.put(p.getId(), p);
        }

        // курсы
        for (var cs : snap.courses) {
            var c = new Course(cs.id, cs.title, cs.credits, cs.professorId);
            courses.put(c.getId(), c);
        }

        // зачисления
        for (var es : snap.enrollments) {
            Grade g = es.grade == null ? null : Grade.valueOf(es.grade);
            var e = new Enrollment(es.id, es.studentId, es.courseId, es.credits, g);
            enrollments.put(e.getId(), e);
        }

        // восстановим обратные связи (составы групп и списки курсов/зачислений)
        snap.courses.forEach(cs -> {
            var c = courses.get(cs.id);
            cs.enrollmentIds.forEach(c::addEnrollment);
        });
        snap.professors.forEach(ps -> {
            var p = professors.get(ps.id);
            ps.courseIds.forEach(p::assignCourse);
        });
        snap.students.forEach(ss -> {
            var s = students.get(ss.id);
            ss.enrollmentIds.forEach(s::addEnrollment);
        });
    }
}
