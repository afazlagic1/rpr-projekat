package ba.unsa.etf.rpr.projekat.data;

import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private int id = -1;
    private List<Child> children = new ArrayList<>();
    private Teacher teacher = new Teacher();
    private static int capacity = 15;

    public Classroom(int id, List<Child> children, Teacher teacher) {
        if(children.size() > capacity)
            throw new ArrayIndexOutOfBoundsException();
        this.id = id;
        this.children = children;
        this.teacher = teacher;
    }

    public Classroom() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        if(children.size() > capacity)
            throw new ArrayIndexOutOfBoundsException();
        this.children = children;
    }

    public void addChildToClassroom(Child child) {
        if(getChildren().size() == capacity)
            throw new ArrayIndexOutOfBoundsException();
        children.add(child);
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capacity) {
        if(capacity < getCapacity())
            throw new ArrayIndexOutOfBoundsException("It is not allowed to make the capacity smaller, because the transfer in some classrooms would possibly be necessary in that case.");
        Classroom.capacity = capacity;
    }
}
