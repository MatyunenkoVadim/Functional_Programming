package university;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Boundary normalization for Snapshot loaded from JSON.
 * Defensive: handles null lists/elements and removes broken references.
 */
final class SnapshotNormalizer {
    private SnapshotNormalizer() {}

    public static Snapshot normalize(Snapshot in) {
        Snapshot src = (in == null) ? new Snapshot() : in;

        Snapshot out = new Snapshot();

        List<Snapshot.StudentSnap> students = safeList(src.students);
        List<Snapshot.ProfessorSnap> professors = safeList(src.professors);
        List<Snapshot.CourseSnap> courses = safeList(src.courses);
        List<Snapshot.EnrollmentSnap> enrollments = safeList(src.enrollments);

        Set<UUID> studentIds = students.stream().map(s -> s.id).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<UUID> professorIds = professors.stream().map(p -> p.id).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<UUID> courseIds = courses.stream().map(c -> c.id).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, Snapshot.EnrollmentSnap> enrollmentById = new LinkedHashMap<>();
        for (Snapshot.EnrollmentSnap e : enrollments) {
            if (e == null) continue;
            if (e.id == null || e.studentId == null || e.courseId == null) continue;
            if (e.credits <= 0) continue;
            if (!studentIds.contains(e.studentId)) continue;
            if (!courseIds.contains(e.courseId)) continue;

            Snapshot.EnrollmentSnap ne = new Snapshot.EnrollmentSnap();
            ne.id = e.id;
            ne.studentId = e.studentId;
            ne.courseId = e.courseId;
            ne.credits = e.credits;
            ne.grade = e.grade;

            enrollmentById.put(ne.id, ne);
        }

        Set<UUID> enrollmentIds = enrollmentById.keySet();

        for (Snapshot.StudentSnap s : students) {
            if (s == null) continue;
            if (s.id == null) continue;
            if (s.name == null || s.name.isBlank()) continue;

            Snapshot.StudentSnap ns = new Snapshot.StudentSnap();
            ns.id = s.id;
            ns.name = s.name.trim();
            ns.enrollmentIds = filterIds(s.enrollmentIds, enrollmentIds);
            out.students.add(ns);
        }

        for (Snapshot.ProfessorSnap p : professors) {
            if (p == null) continue;
            if (p.id == null) continue;
            if (p.name == null || p.name.isBlank()) continue;

            Snapshot.ProfessorSnap np = new Snapshot.ProfessorSnap();
            np.id = p.id;
            np.name = p.name.trim();
            np.courseIds = filterIds(p.courseIds, courseIds);
            out.professors.add(np);
        }

        for (Snapshot.CourseSnap c : courses) {
            if (c == null) continue;
            if (c.id == null) continue;
            if (c.title == null || c.title.isBlank()) continue;
            if (c.credits <= 0) continue;

            Snapshot.CourseSnap nc = new Snapshot.CourseSnap();
            nc.id = c.id;
            nc.title = c.title.trim();
            nc.credits = c.credits;
            nc.professorId = (c.professorId != null && professorIds.contains(c.professorId)) ? c.professorId : null;
            nc.enrollmentIds = filterIds(c.enrollmentIds, enrollmentIds);
            out.courses.add(nc);
        }

        out.enrollments.addAll(enrollmentById.values());

        return out;
    }

    private static <T> List<T> safeList(List<T> src) {
        return (src == null) ? List.of() : src;
    }

    private static List<UUID> filterIds(List<UUID> src, Set<UUID> allowed) {
        List<UUID> list = (src == null) ? List.of() : src;
        ArrayList<UUID> out = new ArrayList<>(list.size());
        for (UUID id : list) {
            if (id != null && allowed.contains(id)) out.add(id);
        }
        return out;
    }
}
