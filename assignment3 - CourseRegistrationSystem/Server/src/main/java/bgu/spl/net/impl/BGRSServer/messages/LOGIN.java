package bgu.spl.net.impl.BGRSServer.messages;

public class LOGIN extends Message {

    private String username;
    private String password;

    public LOGIN(String username, String password) {
        opcode = 3;
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
