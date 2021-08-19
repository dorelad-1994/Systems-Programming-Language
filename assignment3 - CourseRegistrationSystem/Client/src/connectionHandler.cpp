#include <connectionHandler.h>
#include <iostream>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <string>
#include "boost/lexical_cast.hpp"
using namespace std;
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_), shouldTerminate(0){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::send(string &line) {
    istringstream iss(line);
    vector<string> tokens {istream_iterator<string>{iss},istream_iterator<string>{}};
    if (tokens[0] == ("ADMINREG")) {
        char shortCharArr[sizeof(short)];
        shortToBytes(1,shortCharArr);
        return ( sendBytes(shortCharArr, sizeof(short)) && sendLine(tokens[1]) && sendLine(tokens[2]) );
    } else if (tokens[0] == ("STUDENTREG")) {
        char shortCharArr[sizeof(short)];
        shortToBytes(2,shortCharArr);
        return ( sendBytes(shortCharArr, sizeof(short)) && sendLine(tokens[1]) && sendLine(tokens[2]) );
    } else if (tokens[0] == ("LOGIN")) {
        char shortCharArr[sizeof(short)];
        shortToBytes(3,shortCharArr);
        return ( sendBytes(shortCharArr, sizeof(short)) && sendLine(tokens[1]) && sendLine(tokens[2]) );
    } else if (tokens[0] == ("LOGOUT")) {
        char shortCharArr[sizeof(short)];
        shortToBytes(4,shortCharArr);
        return sendBytes(shortCharArr, sizeof(short));
    } else if (tokens[0] == ("COURSEREG")) {
        char opcodeCharArr[sizeof(short)];
        shortToBytes(5,opcodeCharArr);
        char courseNumCharArr[sizeof(short)];
        shortToBytes(stoi(tokens[1]), courseNumCharArr);
        return ( sendBytes(opcodeCharArr, sizeof(short)) && sendBytes(courseNumCharArr, sizeof(short)) );
    } else if (tokens[0] == ("KDAMCHECK")) {
        char opcodeCharArr[sizeof(short)];
        shortToBytes(6,opcodeCharArr);
        char courseNumCharArr[sizeof(short)];
        shortToBytes(boost::lexical_cast<short>(tokens[1]), courseNumCharArr);
        return ( sendBytes(opcodeCharArr, sizeof(short)) && sendBytes(courseNumCharArr, sizeof(short)) );
    } else if (tokens[0] == ("COURSESTAT")) {
        char opcodeCharArr[sizeof(short)];
        shortToBytes(7,opcodeCharArr);
        char courseNumCharArr[sizeof(short)];
        shortToBytes(stoi(tokens[1]), courseNumCharArr);
        return ( sendBytes(opcodeCharArr, sizeof(short)) && sendBytes(courseNumCharArr, sizeof(short)) );
    } else if (tokens[0] == ("STUDENTSTAT")) {
        char shortCharArr[sizeof(short)];
        shortToBytes(8,shortCharArr);
        return ( sendBytes(shortCharArr, sizeof(short)) && sendLine(tokens[1]) );
    } else if (tokens[0] == ("ISREGISTERED")) {
        char opcodeCharArr[sizeof(short)];
        shortToBytes(9,opcodeCharArr);
        char courseNumCharArr[sizeof(short)];
        shortToBytes(stoi(tokens[1]), courseNumCharArr);
        return ( sendBytes(opcodeCharArr, sizeof(short)) && sendBytes(courseNumCharArr, sizeof(short)) );
    } else if (tokens[0] == ("UNREGISTER")) {
        char opcodeCharArr[sizeof(short)];
        shortToBytes(10,opcodeCharArr);
        char courseNumCharArr[sizeof(short)];
        shortToBytes(stoi(tokens[1]), courseNumCharArr);
        return ( sendBytes(opcodeCharArr, sizeof(short)) && sendBytes(courseNumCharArr, sizeof(short)) );
    } else if (tokens[0] == ("MYCOURSES")) {
        char shortCharArr[sizeof(short)];
        shortToBytes(11,shortCharArr);
        return sendBytes(shortCharArr, sizeof(short));
    }
    return false;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    char ch1[2];
    getBytes(ch1, 2);
    short opcode = bytesToShort(ch1);
    char ch2[2];
    getBytes(ch2, 2);
    short messageOpcode = bytesToShort(ch2);
   if (opcode == 12) {
       line = "ACK " + to_string(messageOpcode);
       if ((messageOpcode == 6) | (messageOpcode == 7) | (messageOpcode == 8) | (messageOpcode == 9) | (messageOpcode == 11)) {
           string option;
           bool success = getFrameAscii(option, '\0');
           line += "\n" + option;
           return success;
       }

       return true;
   } else {
       line = "ERROR " + to_string(messageOpcode);
       return true;
   }
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\0');
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;

    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
        do{
            if(!getBytes(&ch, 1))
            {
                return false;
            }
            if(ch!='\0')
                frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.size());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}

short ConnectionHandler::bytesToShort( const char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void ConnectionHandler::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

int ConnectionHandler::getShouldTerminate() {
    return shouldTerminate;
}

void ConnectionHandler::setShouldTerminate(int i) {
    shouldTerminate = i;
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}



