package university;

import java.util.*;

public class Student {
    private final UUID id = Ids.newId();
    private String name;
    private final Set<UUID> enrollmentIds = new LinkedHashSet<>();

    public Student(String name) { setName(name); }

    public UUID getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");
        this.name = name.trim();
    }

    // управление своими данными
    public void addEnrollment(UUID enrollmentId){ enrollmentIds.add(Objects.requireNonNull(enrollmentId)); }
    public void removeEnrollment(UUID enrollmentId){ enrollmentIds.remove(enrollmentId); }
    public Set<UUID> getEnrollmentIds(){ return Collections.unmodifiableSet(enrollmentIds); }

    @Override public String toString(){ return "Student{" + id + ", '" + name + "'}"; }
}
