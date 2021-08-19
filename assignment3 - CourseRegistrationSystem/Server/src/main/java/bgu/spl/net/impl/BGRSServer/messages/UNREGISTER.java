package bgu.spl.net.impl.BGRSServer.messages;

public class UNREGISTER extends Message {

    private short courseNum;

    public UNREGISTER(short courseNum) {
        opcode = 10;
        this.courseNum = courseNum;
    }

    public short getCourseNum() {
        return courseNum;
    }
}
