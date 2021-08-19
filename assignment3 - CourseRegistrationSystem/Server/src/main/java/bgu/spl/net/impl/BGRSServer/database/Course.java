package bgu.spl.net.impl.BGRSServer.database;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class Course {

    private short id;
    private String name;
    private short[] kdam;
    private int maxStudentsNum;
    private int registeredStudentsNum;
    private LinkedList<String> registeredStudents;

    public Course(String[] courseInfo) {
        this.id = Short.parseShort(courseInfo[0]);
        this.name = courseInfo[1];
        String kdamString = courseInfo[2];
        if (kdamString.length() > 2) {
            kdamString = kdamString.substring(1, courseInfo[2].length() - 1);
            String[] kdamStringArray = kdamString.split(",");
            this.kdam = new short[kdamStringArray.length];
            for (int i = 0; i < kdam.length; i++) {
                kdam[i] = Short.parseShort(kdamStringArray[i]);
            }
        }

        this.maxStudentsNum = Integer.parseInt(courseInfo[3]);
        registeredStudentsNum = 0;
        registeredStudents = new LinkedList<>();
    }

    public void sortKdamByOrder(LinkedList<Short> coursesByOrder) {
        if (kdam != null) {
            short[] newKdam = new short[kdam.length];
            int index = 0;
            for (Short c1 : coursesByOrder) {
                for (short c2 : kdam) {
                    if (c1 == c2 & index < kdam.length) {
                        newKdam[index] = c2;
                        index++;
                    }
                }
            }
            kdam = newKdam;
        }
    }

    public short getId() {
        return id;
    }

    public short[] getKdam() {
        return kdam;
    }

    public boolean register(String username) {
        synchronized (registeredStudents) {
            if (registeredStudentsNum < maxStudentsNum) {
                registeredStudents.add(username);
                registeredStudentsNum++;
                return true;
            }
            return false;
        }
    }

    public String getKdamString() {
        String output = "[";
        if (kdam != null) {
            for (int i = 0; i < kdam.length - 1; i++) {
                output = output + kdam[i] + ",";
            }
            output = output + kdam[kdam.length - 1];
        }
        output += "]";
        return output;
    }

    public String getCourseStat() {
        synchronized (registeredStudents) {// to prevent a student from registering to this course while we are getting the stat
            String output = "Course: " + "(" + id + ") " + name + "\n";
            output = output + "Seats Available: " + (maxStudentsNum - registeredStudentsNum) + "/" + maxStudentsNum + "\n";
            output = output + "Students Registered: [";
            registeredStudents.sort(Comparator.naturalOrder());
            int counter = registeredStudents.size();
            for (String username : registeredStudents) {
                output = output + username;
                if (counter != 1) {
                    output = output + ",";
                }
                counter--;
            }
            output += "]";
            return output;
        }
    }

    public void unregisterStudent(String username) {
        synchronized (registeredStudents) {
            registeredStudents.remove(username);
            registeredStudentsNum--;
        }
    }

}
