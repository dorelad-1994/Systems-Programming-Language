package bgu.spl.net.impl.BGRSServer.messages;

public class ADMINREG extends Message {

    private String username;
    private String password;

    public ADMINREG(String username, String password) {
        opcode = 1;
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
