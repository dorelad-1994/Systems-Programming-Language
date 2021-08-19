package bgu.spl.net.impl.BGRSServer.database;
import java.util.HashMap;
import java.util.LinkedList;

public class Student extends User {

    private HashMap<Short, Boolean> courses;

    public Student (String username, String password) {
        super(username,password);
        courses = new HashMap<>();
    }

    public boolean isRegisteredToCourse (short courseNum) {
        return courses.containsKey(courseNum);
    }

    public void registerToCourse(short courseNum) {
        courses.put(courseNum, true);
    }

    public int numOfCoursesRegistered() {
        return courses.size();
    }

    public void unregisterFromCourse(short courseNum) {
        courses.remove(courseNum);
    }

    public String getMyCourses(LinkedList<Short> coursesByOrder) {
        String output = "[";
        int counter = courses.size();
        for (Short course: coursesByOrder) {
            if (courses.containsKey(course)) {
                output = output + course;
                if (counter != 1) {
                    output = output + ",";
                }
                counter--;
            }
        }
        output += "]";
        return output;
    }

}
