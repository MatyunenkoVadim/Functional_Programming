package university;

import java.util.List;

final class QueryResults {
    private QueryResults() {}

    public record Transcript(Student student, List<String> lines, double gpa) {}

    public record Roster(Course course, Professor professorOrNull, List<Student> students) {}

    public record ProfessorCourses(Professor professor, List<Course> courses) {}

    public record SearchResult(String query, List<Student> students, List<Course> courses) {}
}
