//
// Created by spl211 on 05/11/2020.
//
#include <fstream>
#include "Session.h"
#include "Agent.h"
#include "json.hpp"
#include "iostream"

#define JSON_PATH "output.json"
using namespace std;
using json = nlohmann::json;

// normal constructor
Session::Session(const std::string &path) : g(vector < vector < int >> (0)), treeType(), agents(), cycleCounter(-1),
                                            infectedQueue(queue<int>()),
                                            nodeHasVirus(vector<bool>(0)), infectedNodes(vector<int>(0)),
                                            endOfCycle(0) {
    //read data from json
    ifstream i(path);
    json j;
    i >> j;
    g = Graph(j["graph"]);
    nodeHasVirus.resize(g.getEdges().size());
    for (auto &k : j["agents"]) {
        if (k[0] == "V") {
            agents.push_back(new Virus(k[1]));
            nodeHasVirus[k[1]] = true;  //init all nodes false except the node of virus.
        }
        if (k[0] == "C") {
            agents.push_back(new ContactTracer());
        }
    }
    if (j["tree"] == "M") {
        treeType = MaxRank;
    }
    if (j["tree"] == "C") {
        treeType = Cycle;
    }
    if (j["tree"] == "R") {
        treeType = Root;
    }
    endOfCycle = agents.size();
}


//copy constructor
Session::Session(const Session &other) : g(other.g), treeType(other.treeType), agents(vector<Agent *>(0)),
                                         cycleCounter(other.cycleCounter), infectedQueue(other.infectedQueue),
                                         nodeHasVirus(other.nodeHasVirus), infectedNodes(vector<int>(0)),
                                         endOfCycle(other.endOfCycle) {
    copy(other);  //copy Agents
}


// operator=
const Session &Session::operator=(const Session &other) {
    if (this != &other) {
        clearAgents();    // clear my agents
        g = other.g;
        treeType = other.treeType;
        cycleCounter = other.cycleCounter;
        infectedQueue = other.infectedQueue;
        nodeHasVirus = other.nodeHasVirus;
        endOfCycle = other.endOfCycle;
        copy(other);  // deep copy of other.agents
    }
    return *this;
}


//move constructor
Session::Session(Session &&other) : g(other.g), treeType(other.treeType), agents(move(other.agents)),
                                    cycleCounter(other.cycleCounter), infectedQueue(other.infectedQueue),
                                    nodeHasVirus(other.nodeHasVirus), infectedNodes(vector<int>(0)),
                                    endOfCycle(other.endOfCycle) {
}


//move assignment operator
const Session &Session::operator=(Session &&other) {
    if(this != &other) {
        clearAgents();  //clear  my agents
        g = other.g;
        treeType = other.treeType;
        cycleCounter = other.cycleCounter;
        infectedQueue = other.infectedQueue;
        nodeHasVirus = other.nodeHasVirus;
        endOfCycle = other.endOfCycle;
        agents = move(other.agents);  // "steals" the other.agents
    }
    return *this;
}

void Session::copy(const Session &other) {  //deep copy
    for (auto a : other.agents) {
        agents.push_back(a->clone());
    }
}

void Session::clearAgents() {
    for (auto a : agents) {  //safe delete
        if (a) {
            delete a;
            a = nullptr;
        }
    }
    agents.clear();
}


void Session::simulate() {
    bool canContinue(true);
    while (canContinue) { //If no agent can continue the session stops
        cycleCounter++;
        endOfCycle = agents.size(); // to know which agents will act this cycle
        canContinue = false;
        for (int i = 0; i < endOfCycle; i++) {
            agents[i]->act(*this);
            canContinue = (canContinue || agents[i]->getCanContinue()); //if all agents can't continue the simulation ends.
        }

    }
    output();// Creates output json file
}


void Session::setGraph(const Graph &graph) {
    g = graph;
}

void Session::addAgent(const Agent &agent) {
    agents.push_back(agent.clone());
}

int Session::dequeueInfected() {
    int i = infectedQueue.front();
    infectedQueue.pop();
    return i;
}

void Session::enqueueInfected(int node) {
    infectedQueue.push(node);
    infectedNodes.push_back(node);
    g.infectNode(node);
}

bool Session::hasVirus(int nodeInd) const {
    return nodeHasVirus[nodeInd];
}

void Session::output() const {  //create output json file
    json j;
    j["graph"] = g.getEdges();
    j["infected"] = infectedNodes;
    std::ofstream o(JSON_PATH);
    o << j << endl;
}

bool Session::infectedQueueIsEmpty() const {
    return infectedQueue.empty();
}

void Session::setNodeHasVirus(int ind) {
    nodeHasVirus[ind] = true;
}

TreeType Session::getTreeType() const {
    return treeType;
}

const Graph &Session::getGraph() const {
    return g;
}

const int Session::getCycleCounter() const {
    return cycleCounter;
}

void Session::disconnect(int node) {
    g.disconnect(node);
}

// destructor
Session::~Session() {
    clearAgents();
}
