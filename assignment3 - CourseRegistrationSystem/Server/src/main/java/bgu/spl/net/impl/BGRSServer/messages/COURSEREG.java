package bgu.spl.net.impl.BGRSServer.messages;

public class COURSEREG extends Message {

    private short courseNum;

    public COURSEREG(short courseNum) {
        opcode = 5;
        this.courseNum = courseNum;
    }

    public short getCourseNum() {
        return courseNum;
    }
}
