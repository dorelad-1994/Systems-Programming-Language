package bgu.spl.net.impl.BGRSServer.messages;

public class ERROR extends Message {

    private short  messageOpcode;

    public ERROR(short messageOpcode) {
        opcode = 13;
        this.messageOpcode = messageOpcode;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }
}
