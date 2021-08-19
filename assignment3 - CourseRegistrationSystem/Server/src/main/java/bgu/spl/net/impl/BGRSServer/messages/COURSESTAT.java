package bgu.spl.net.impl.BGRSServer.messages;

public class COURSESTAT extends Message {

    private short courseNum;

    public COURSESTAT(short courseNum) {
        opcode = 7;
        this.courseNum = courseNum;
    }

    public short getCourseNum() {
        return courseNum;
    }
}
