package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.messages.Message;

public interface Respondable {
    Message respond (Message msg);
}
