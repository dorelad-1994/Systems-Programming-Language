package bgu.spl.net.impl.BGRSServer.messages;

public class STUDENTSTAT extends Message {

    private String username;

    public STUDENTSTAT(String username) {
        opcode = 8;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
