//
// Created by spl211 on 05/11/2020.
//
#include "Graph.h"
#include <iostream>
using namespace std;

Graph::Graph(vector<vector<int>> matrix) : edges(matrix), infectedOrNot(vector<bool>(edges.size())) { }

void Graph::infectNode(int nodeInd) {//Maintains a boolean array for the function isInfected
    infectedOrNot[nodeInd] = true;
}


bool Graph::isInfected(int nodeInd) const {
    return infectedOrNot[nodeInd];
}



bool Graph::areNeighbours(int a, int b) const {
    return (edges[a][b] == 1);
}

void Graph::disconnect(int node) {//Removes all the edges of a node disconnecting it from the graph
    for (auto & i : edges[node]) {
        i = 0;
    }
    for (auto & i : edges) {
        i[node] = 0;
    }
}

const vector<vector<int>> & Graph::getEdges() const {
    return edges;
}




