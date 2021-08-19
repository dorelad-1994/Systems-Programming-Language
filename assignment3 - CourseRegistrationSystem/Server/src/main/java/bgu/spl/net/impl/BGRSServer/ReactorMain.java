package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args)  {

        if (args.length < 2) {
            throw new IllegalArgumentException();
        }

        Database database = Database.getInstance();
        database.initialize("./Courses.txt");

        try (Server s = Server.reactor(Integer.parseInt(args[1]), Integer.parseInt(args[0]), BGRSProtocol::new, BGRSMessageEncoderDecoder::new)) {
            s.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
