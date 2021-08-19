package bgu.spl.net.impl.BGRSServer.messages;

public class KDAMCHECK extends Message {

    private short courseNum;

    public KDAMCHECK(short courseNum) {
        opcode = 6;
        this.courseNum = courseNum;
    }

    public short getCourseNum() {
        return courseNum;
    }
}
