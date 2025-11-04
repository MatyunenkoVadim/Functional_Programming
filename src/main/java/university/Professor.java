package university;

import java.util.*;

public class Professor {
    private final UUID id = Ids.newId();
    private String name;
    private final Set<UUID> courseIds = new LinkedHashSet<>();

    public Professor(String name){ setName(name); }

    public UUID getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");
        this.name = name.trim();
    }

    public void assignCourse(UUID courseId){ courseIds.add(Objects.requireNonNull(courseId)); }
    public void unassignCourse(UUID courseId){ courseIds.remove(courseId); }
    public Set<UUID> getCourseIds(){ return Collections.unmodifiableSet(courseIds); }

    @Override public String toString(){ return "Professor{" + id + ", '" + name + "'}"; }
}
