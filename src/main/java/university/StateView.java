package university;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

interface StateView {
    Optional<Student> student(UUID id);
    Optional<Professor> professor(UUID id);
    Optional<Course> course(UUID id);
    Optional<Enrollment> enrollment(UUID id);

    Collection<Student> students();
    Collection<Professor> professors();
    Collection<Course> courses();
    Collection<Enrollment> enrollments();
}