package bgu.spl.net.impl.BGRSServer.messages;

public class ACK extends Message {

    private short  messageOpcode;
    private String dataForClient;

    public ACK(short messageOpcode, String dataForClient) {
        opcode = 12;
        this.messageOpcode = messageOpcode;
        this.dataForClient = dataForClient;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }

    public String getDataForClient() {
        return dataForClient;
    }
}
