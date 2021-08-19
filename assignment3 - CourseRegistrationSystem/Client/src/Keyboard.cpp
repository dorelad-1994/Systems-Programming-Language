//
// Created by spl211 on 04/01/2021.
//

#include "Keyboard.h"
#include <mutex>
#include <iostream>
#include <connectionHandler.h>

Keyboard::Keyboard(ConnectionHandler &connectionHandler) : _connectionHandler(connectionHandler) {}

void Keyboard::run() {
    while (_connectionHandler.getShouldTerminate() == 0) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (!_connectionHandler.send(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if (line == "LOGOUT") {
            bool answerRecieved = false;
            while(!answerRecieved) {
                if (_connectionHandler.getShouldTerminate() == 1) {
                    _connectionHandler.setShouldTerminate(0);
                    answerRecieved = true;
                } else if (_connectionHandler.getShouldTerminate() == -1) {
                    answerRecieved = true;
                }
            }
        }

    }

}

