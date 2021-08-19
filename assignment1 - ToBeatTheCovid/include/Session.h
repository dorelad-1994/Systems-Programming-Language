#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <string>
#include "Graph.h"

class Agent;

enum TreeType{
  Cycle,
  MaxRank,
  Root
};

class Session{
public:
    Session(const std::string& path);
    Session(const Session& other);
    const Session& operator=(const Session& other);
    Session(Session&& other);
    const Session& operator=(Session&& other);

    void copy(const Session& other);
    void clearAgents();

    void simulate();
    void setGraph(const Graph& graph);
    void addAgent(const Agent& agent);

    int dequeueInfected();
    void enqueueInfected(int node);

    bool hasVirus(int nodeInd) const;
    void output() const;
    bool infectedQueueIsEmpty() const;
    void setNodeHasVirus(int ind);

    TreeType getTreeType() const;
    const Graph & getGraph() const;
    const int getCycleCounter() const;
    void disconnect(int node);


    virtual ~Session();
    
private:
    Graph g;
    TreeType treeType;
    std::vector<Agent*> agents;
    int cycleCounter;
    queue<int> infectedQueue;
    vector<bool> nodeHasVirus;
    vector<int> infectedNodes;
    int endOfCycle;
};

#endif
