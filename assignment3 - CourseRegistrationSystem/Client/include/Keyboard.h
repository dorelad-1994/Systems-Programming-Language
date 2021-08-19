//
// Created by spl211 on 04/01/2021.
//
#include <mutex>
#include "connectionHandler.h"

#ifndef BOOST_ECHO_CLIENT_TASK_H
#define BOOST_ECHO_CLIENT_TASK_H


class Keyboard {
private:
    ConnectionHandler& _connectionHandler;
public:
    Keyboard(ConnectionHandler& connectionHandler);
    void run();
};


#endif //BOOST_ECHO_CLIENT_TASK_H
