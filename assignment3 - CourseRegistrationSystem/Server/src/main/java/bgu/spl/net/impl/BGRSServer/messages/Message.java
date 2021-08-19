package bgu.spl.net.impl.BGRSServer.messages;

public abstract class Message {

    protected short opcode;

    public short getOpcode() {
        return opcode;
    }
}
