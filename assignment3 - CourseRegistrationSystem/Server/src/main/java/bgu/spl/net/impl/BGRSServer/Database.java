package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.database.Admin;
import bgu.spl.net.impl.BGRSServer.database.Course;
import bgu.spl.net.impl.BGRSServer.database.Student;
import bgu.spl.net.impl.BGRSServer.database.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Passive object representing the bgu.spl.net.impl.bgrs.Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private static class DatabaseSingletonHolder {
        private static Database singleton = new Database();
    }

    private ConcurrentHashMap<String, Boolean> loggedIn;
    private ConcurrentHashMap<String, Admin> admins;
    private ConcurrentHashMap<String, Student> students;
    private ConcurrentHashMap<Short, Course> courses;
    private LinkedList<Short> coursesByOrder;

    //to prevent user from creating new Database
    private Database() {
        loggedIn = new ConcurrentHashMap<>();
        admins = new ConcurrentHashMap<>();
        students = new ConcurrentHashMap<>();
        courses = new ConcurrentHashMap<>();
        coursesByOrder = new LinkedList<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseSingletonHolder.singleton;
    }

    /**
     * loades the courses from the file path specified
     * into the bgu.spl.net.impl.bgrs.Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(coursesFilePath));
            String line = reader.readLine();
            while (line != null) {
                String[] courseInfo = line.split("\\|");
                coursesByOrder.add(Short.parseShort(courseInfo[0]));
                Course course = new Course(courseInfo);
                courses.put(course.getId(), course);
                line = reader.readLine();
            }
            for (Short course : coursesByOrder) {
                courses.get(course).sortKdamByOrder(coursesByOrder);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerAdmin(String username, String password) {
        Boolean b = loggedIn.putIfAbsent(username, false);
        if (b != null) {
            return false;
        } else {
            Admin admin = new Admin(username, password);
            admins.put(username, admin);
            return true;
        }
    }

    public boolean registerStudent(String username, String password) {
        Boolean b = loggedIn.putIfAbsent(username, false);
        if (b != null) {
            return false;
        } else {
            Student student = new Student(username, password);
            students.put(username, student);
            return true;
        }
    }

    public boolean logInUser(String username, String password) {
        User u = admins.get(username);
        if (u == null) {
            u = students.get(username);
        }
        if (u == null) {
            return false;
        }
        String existingPassword = u.getPassword();
        if (existingPassword.equals(password)) {
            Boolean b = loggedIn.replace(username, true);
            if (b == null || b) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void logOutUser(String username) {
        loggedIn.replace(username, false);
    }

    public boolean registerToCourse(String username, short courseNum) {
        Course course = courses.get(courseNum);
        Student student = students.get(username);
        if (course == null || student == null || student.isRegisteredToCourse(courseNum) || !hasAllKdam(student, courseNum)) {
            return false;
        }
        boolean success = course.register(username);
        if (success) {
            student.registerToCourse(courseNum);
        }
        return success;
    }

    private boolean hasAllKdam(Student student, short courseNum) {
        boolean output = true;
        short[] kdam = courses.get(courseNum).getKdam();
        for (int i = 0; kdam != null && output && i < kdam.length; i++) {
            if (!student.isRegisteredToCourse(kdam[i])) {
                output = false;
            }
        }
        return output;
    }

    public String getAllKDAM(String username, short courseNum) {
        Course course = courses.get(courseNum);
        Student student = students.get(username);
        if (course == null | student == null) {
            return null;
        }
        return course.getKdamString();
    }

    public String getCourseStat(String username, short courseNum) {
        Course course = courses.get(courseNum);
        Admin admin = admins.get(username);
        if (course == null | admin == null) {
            return null;
        }
        return course.getCourseStat();
    }

    public String getStudentStat(String adminUsername, String studentUsername) {
        Admin admin = admins.get(adminUsername);
        Student student = students.get(studentUsername);
        if (admin == null | student == null) {
            return null;
        }
        String output = "Student: " + student.getUsername() + "\n";
        output = output + "Courses: [";
        int counter = student.numOfCoursesRegistered();
        for (short id : coursesByOrder) {
            if (student.isRegisteredToCourse(id)) {
                output = output + id;
                if (counter != 1) {
                    output = output + ",";
                }
                counter--;
            }
        }
        output += "]";
        return output;
    }

    public boolean isRegisteredToCourse(String username, short courseNum) {
        Course course = courses.get(courseNum);
        Student student = students.get(username);
        if (course == null | student == null) {
            return false;
        }
        return student.isRegisteredToCourse(courseNum);
    }

    public boolean unregisterFromCourse(String username, short courseNum) {
        Course course = courses.get(courseNum);
        Student student = students.get(username);
        if (course == null | student == null || !student.isRegisteredToCourse(courseNum)) {
            return false;
        }
        course.unregisterStudent(username);
        student.unregisterFromCourse(courseNum);
        return true;
    }

    public String getMyCourses(String username) {
        Student student = students.get(username);
        if (student == null) {
            return null;
        }
        return student.getMyCourses(coursesByOrder);
    }

    public boolean isAdmin(String username) {
        return admins.containsKey(username);
    }

}