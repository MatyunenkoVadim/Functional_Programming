package university;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Доменные команды
 */
public final class RegistrarCommands {
    private RegistrarCommands() {
    }

    public record Result<T>(State state, T value) {
        public Result {
            Objects.requireNonNull(state, "state");
        }
    }

    public static void requireStudentExists(State st, UUID studentId) {
        Objects.requireNonNull(studentId, "studentId");
        if (!st.studentsMap().containsKey(studentId)) throw new IllegalArgumentException("student not found");
    }

    public static void requireProfessorExists(State st, UUID professorId) {
        Objects.requireNonNull(professorId, "professorId");
        if (!st.professorsMap().containsKey(professorId)) throw new IllegalArgumentException("professor not found");
    }

    public static void requireCourseExists(State st, UUID courseId) {
        Objects.requireNonNull(courseId, "courseId");
        if (!st.coursesMap().containsKey(courseId)) throw new IllegalArgumentException("course not found");
    }

    public static Result<Enrollment> enroll(State st, UUID studentId, UUID courseId) {
        Objects.requireNonNull(st, "state");
        requireStudentExists(st, studentId);
        requireCourseExists(st, courseId);

        if (findEnrollment(st, studentId, courseId).isPresent()) {
            throw new IllegalStateException("Студент уже записан на этот курс");
        }

        Course c = st.coursesMap().get(courseId);
        Enrollment e = new Enrollment(studentId, courseId, c.getCredits());

        State next = apply(st,
                s -> StateOps.putEnrollment(s, e),
                s -> StateOps.updateStudent(s, studentId, stt -> stt.withAddedEnrollment(e.getId())),
                s -> StateOps.updateCourse(s, courseId, crs -> crs.withAddedEnrollment(e.getId()))
        );
        return new Result<>(next, e);
    }

    public static State drop(State st, UUID studentId, UUID courseId) {
        Objects.requireNonNull(st, "state");
        requireStudentExists(st, studentId);
        requireCourseExists(st, courseId);

        Enrollment e = findEnrollment(st, studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        return apply(st,
                s -> StateOps.removeEnrollment(s, e.getId()),
                s -> StateOps.updateStudent(s, studentId, stt -> stt.withRemovedEnrollment(e.getId())),
                s -> StateOps.updateCourse(s, courseId, crs -> crs.withRemovedEnrollment(e.getId()))
        );
    }

    public static State grade(State st, UUID studentId, UUID courseId, Grade grade) {
        Objects.requireNonNull(st, "state");
        Objects.requireNonNull(grade, "grade");
        requireStudentExists(st, studentId);
        requireCourseExists(st, courseId);

        Enrollment e = findEnrollment(st, studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        return StateOps.putEnrollment(st, e.withGrade(grade));
    }

    public static State assignProfessor(State st, UUID courseId, UUID professorId) {
        Objects.requireNonNull(st, "state");
        requireCourseExists(st, courseId);
        requireProfessorExists(st, professorId);

        Course curCourse = st.coursesMap().get(courseId);
        UUID oldProfId = curCourse.getProfessorId().orElse(null);

        State next = st;

        if (oldProfId != null && !oldProfId.equals(professorId)) {
            // снять курс у старого преподавателя
            next = StateOps.updateProfessor(next, oldProfId, p -> p.withUnassignedCourse(courseId));
        }

        // назначить нового
        next = StateOps.updateCourse(next, courseId, c -> c.withProfessor(professorId));
        next = StateOps.updateProfessor(next, professorId, p -> p.withAssignedCourse(courseId));

        return next;
    }

    public static State removeCourse(State st, UUID courseId) {
        Objects.requireNonNull(st, "state");
        requireCourseExists(st, courseId);

        Course c = st.coursesMap().get(courseId);

        State next = st;

        // снять курс у преподавателя, если был назначен
        UUID oldProfId = c.getProfessorId().orElse(null);
        if (oldProfId != null) {
            next = StateOps.updateProfessor(next, oldProfId, p -> p.withUnassignedCourse(courseId));
        }

        // удалить зачисления + почистить студентов
        for (UUID enrId : c.getEnrollmentIds()) {
            Enrollment e = next.enrollmentsMap().get(enrId);
            if (e == null) continue;

            next = StateOps.removeEnrollment(next, enrId);
            next = StateOps.updateStudent(next, e.getStudentId(), s -> s.withRemovedEnrollment(enrId));
        }

        // удалить курс
        next = StateOps.removeCourse(next, courseId);

        return next;
    }

    private static Optional<Enrollment> findEnrollment(State st, UUID studentId, UUID courseId) {
        return st.enrollmentsMap().values().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst();
    }

    @SafeVarargs
    private static State apply(State st, java.util.function.UnaryOperator<State>... steps) {
        return ImmutableOps.pipe(steps).apply(st);
    }
}
