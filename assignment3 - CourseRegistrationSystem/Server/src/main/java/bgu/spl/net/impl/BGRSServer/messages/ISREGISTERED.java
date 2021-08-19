package bgu.spl.net.impl.BGRSServer.messages;

public class ISREGISTERED extends Message {

    private short courseNum;

    public ISREGISTERED(short courseNum) {
        opcode = 9;
        this.courseNum = courseNum;
    }

    public short getCourseNum() {
        return courseNum;
    }
}
