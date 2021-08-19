package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.BGRSServer.messages.*;
import java.util.HashMap;

public class BGRSProtocol implements MessagingProtocol<Message> {

    private boolean shouldTerminate = false;
    private String username;
    private Database database = Database.getInstance();
    private HashMap<Short, Respondable> responses;

    public BGRSProtocol() {
        responses = new HashMap<>();
        init();
    }

    @Override
    public Message process(Message msg) {
        Respondable r = responses.get(msg.getOpcode());
        Message m = r.respond(msg);
        return m;
    }

    private Message respondADMINREG(ADMINREG msg) {
        if (username == null) {
            boolean success = database.registerAdmin(msg.getUsername(), msg.getPassword());
            if (!success) {
                return new ERROR(msg.getOpcode());
            } else {
                return new ACK(msg.getOpcode(), "");
            }
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondSTUDENTREG(STUDENTREG msg) {
        if (username == null) {
            boolean success = database.registerStudent(msg.getUsername(), msg.getPassword());
            if (!success) {
                return new ERROR(msg.getOpcode());
            } else {
                return new ACK(msg.getOpcode(), "");
            }
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondLOGIN(LOGIN msg) {
        if (username == null && database.logInUser(msg.getUsername(), msg.getPassword())) {
            username = msg.getUsername();
            return new ACK(msg.getOpcode(), "");
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondLOGOUT(LOGOUT msg) {
        if (username != null) {
            database.logOutUser(username);
            username = null;
            return new ACK(msg.getOpcode(), "");
        } else {
            return new ERROR(msg.getOpcode());
        }
    }

    private Message respondCOURSEREG(COURSEREG msg) {
        if (username != null) {
            boolean success = database.registerToCourse(username, msg.getCourseNum());
            if (success) {
                return new ACK(msg.getOpcode(), "");
            }
        }

        return new ERROR(msg.getOpcode());
    }

    private Message respondKDAMCHECK(KDAMCHECK msg) {
        if (username != null) {
            String answer = database.getAllKDAM(username, msg.getCourseNum());
            if (answer != null) {
                return new ACK(msg.getOpcode(), answer);
            }
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondCOURSESTAT(COURSESTAT msg) {
        if (username != null) {
            String answer = database.getCourseStat(username, msg.getCourseNum());
            if (answer != null) {
                return new ACK(msg.getOpcode(), answer);
            }
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondSTUDENTSTAT(STUDENTSTAT msg) {
        if (username != null) {
            String answer = database.getStudentStat(username, msg.getUsername());
            if (answer != null) {
                return new ACK(msg.getOpcode(), answer);
            }
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondISREGISTERED(ISREGISTERED msg) {
        if (username != null && !database.isAdmin(username)) {
            boolean isRegistered = database.isRegisteredToCourse(username, msg.getCourseNum());
            if (isRegistered) {
                return new ACK(msg.getOpcode(), "REGISTERED");
            }
            return new ACK(msg.getOpcode(), "NOT REGISTERED");
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondUNREGISTER(UNREGISTER msg) {
        if (username != null && !database.isAdmin(username)) {
            boolean success = database.unregisterFromCourse(username, msg.getCourseNum());
            if (success) {
                return new ACK(msg.getOpcode(), "");
            }
        }
        return new ERROR(msg.getOpcode());
    }

    private Message respondMYCOURSES(MYCOURSES msg) {
        if (username != null) {
            String answer = database.getMyCourses(username);
            if (answer != null) {
                return new ACK(msg.getOpcode(), answer);
            }
        }
        return new ERROR(msg.getOpcode());
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private void init() {
        responses.put((short) 1, (m) -> respondADMINREG((ADMINREG) m));
        responses.put((short) 2, (m) -> respondSTUDENTREG((STUDENTREG) m));
        responses.put((short) 3, (m) -> respondLOGIN((LOGIN) m));
        responses.put((short) 4, (m) -> respondLOGOUT((LOGOUT) m));
        responses.put((short) 5, (m) -> respondCOURSEREG((COURSEREG) m));
        responses.put((short) 6, (m) -> respondKDAMCHECK((KDAMCHECK) m));
        responses.put((short) 7, (m) -> respondCOURSESTAT((COURSESTAT) m));
        responses.put((short) 8, (m) -> respondSTUDENTSTAT((STUDENTSTAT) m));
        responses.put((short) 9, (m) -> respondISREGISTERED((ISREGISTERED) m));
        responses.put((short) 10, (m) -> respondUNREGISTER((UNREGISTER) m));
        responses.put((short) 11, (m) -> respondMYCOURSES((MYCOURSES) m));
    }
}