#ifndef GRAPH_H_
#define GRAPH_H_

#include <vector>
#include <queue>

using namespace std;

class Graph{
public:
    Graph(std::vector<std::vector<int>> matrix);

    void infectNode(int nodeInd);
    bool isInfected(int nodeInd) const;
    bool areNeighbours(int a, int b) const;
    void disconnect (int node);
    const vector<vector<int>> & getEdges() const;
private:
    std::vector<std::vector<int>> edges;
//    queue<int> infectedQueue;
    vector<bool> infectedOrNot;
};

#endif
