#include <stdlib.h>
#include <connectionHandler.h>
#include <iostream>
#include <mutex>
#include <thread>
#include "Keyboard.h"
#include <boost/thread.hpp>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
	//From here we will see the rest of the ehco client implementation:
    Keyboard k(connectionHandler);
    boost::thread th(&Keyboard::run, &k);
    while (true) {
        std::string answer;
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        std::cout << answer << endl;

        if (answer == "ACK 4") {
            connectionHandler.setShouldTerminate(-1);
            break;
        } else if (answer == "ERROR 4") {
            connectionHandler.setShouldTerminate(1);
        }
    }
    th.join();
    connectionHandler.close();
    return 0;
}
//bin/echoExample 127.0.0.1 7777