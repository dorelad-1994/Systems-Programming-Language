CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: Client
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/Main.o bin/Keyboard.o $(LDFLAGS) 

Client: bin/connectionHandler.o bin/Main.o bin/Keyboard.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/Main.o: src/Main.cpp
	g++ $(CFLAGS) -o bin/Main.o src/Main.cpp

bin/Keyboard.o: src/Keyboard.cpp
	g++ $(CFLAGS) -o bin/Keyboard.o src/Keyboard.cpp

	
.PHONY: clean
clean:
	rm -f bin/*
