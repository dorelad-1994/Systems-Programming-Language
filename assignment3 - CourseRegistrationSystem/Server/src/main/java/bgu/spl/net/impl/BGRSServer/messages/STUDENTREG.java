package bgu.spl.net.impl.BGRSServer.messages;

public class STUDENTREG extends Message {

    private String username;
    private String password;

    public STUDENTREG(String username, String password) {
        opcode = 2;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
