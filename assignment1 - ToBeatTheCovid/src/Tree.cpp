//
// Created by spl211 on 05/11/2020.
//

#include "Tree.h"
#include "Session.h"
#include <queue>
#include <iostream>
#include <algorithm>
#include <climits>

using namespace std;

class Session;

// -------------------------------------------------  Tree  ------------------------------------------------------------

//Normal Constructor
Tree::Tree(int rootLabel) : node(rootLabel), children(vector<Tree *>(0)), depth(0) {}

//Copy Constructor
Tree::Tree(const Tree &other) : node(other.node), children(vector<Tree *>(0)), depth(other.depth) {
    copy(other);
}

//Copy Assignment Operator
const Tree &Tree::operator=(const Tree &other) {
    if (this != &other) {
        clearChildren(); // clear my children
        node = other.node;
        depth = other.depth;
        copy(other); // deep copy other.children
    }
    return *this;
}

//Move Constructor
Tree::Tree(Tree &&other) : node(other.node), children(move(other.children)), depth(other.depth) {}

//Move Assignment Operator
const Tree &Tree::operator=(Tree &&other) {
    if (this != &other) {
        clearChildren();
        node = other.node;
        depth = other.depth;
        children = move(other.children);//"Steals" other's children
    }
    return *this;
}

void Tree::copy(const Tree &other) {
    for (unsigned i = 0; i < other.children.size(); i++) {
        Tree *child = other.children[i]->clone();
        children.push_back(child);
    }
}

void Tree::clearChildren() {
    for (auto a : children) {
        if (a) {
            delete a;
            a = nullptr;
        }
    }
    children.clear();
}

Tree *Tree::createTree(const Session &session, int rootLabel) {
    TreeType t = session.getTreeType();
    switch (t) {
        case MaxRank : {
            return new MaxRankTree(rootLabel);
        }
        case Cycle : {
            return new CycleTree(rootLabel, session.getCycleCounter());
        }
        case Root : {
            return new RootTree(rootLabel);
        }
    }
    return nullptr;
}

//Adds a clone of the argument to my children
void Tree::addChild(const Tree &child) {
    Tree *clone = child.clone();
    children.push_back(clone);
    clone->depth = depth + 1;
}

const vector<Tree *> &Tree::getChildren() const {
    return children;
}

int Tree::getNode() const { return node; }

int Tree::getDepth() const { return depth; }

//*this becomes a BFS tree at the end of this function
void Tree::BFS(const Session &session) {
    if (session.getTreeType() != Root) {// No need to run BFS, we always return the root
        queue<Tree *> q;
        Graph g = session.getGraph();
        vector<bool> visited(g.getEdges().size());//if visited[i]=true then node i was visited
        Tree *parent = this;
        q.push(parent);
        visited.operator[](parent->node) = true;
        while (!q.empty()) {
            parent = q.front();
            q.pop();
            for (unsigned i = 0; i < g.getEdges().size(); i++) {
               //If the node hasn't been visited and is a neighbour of parent:
                if (!visited[i] && g.areNeighbours(parent->node, i)) {
                    Tree *child = createTree(session, i);
                    parent->addChild(*child);//Adds a copy of child to children
                    visited.operator[](child->node) = true;
                    Tree *copy = parent->children.back();
                    q.push(copy);//Pushes to the queue the last child that was added to children
                    if (child) {//Handling dynamic storage allocated to child
                        delete child;
                        child = nullptr;
                    }
                }
            }
        }
    }
}

Tree::~Tree() {
    clearChildren();
}

// -------------------------------------------------  CycleTree  ------------------------------------------------------------

CycleTree::CycleTree(int rootLabel, int currCycle) : Tree(rootLabel), currCycle(currCycle) {}

Tree *CycleTree::clone() const {
    return new CycleTree(*this);
}

int CycleTree::traceTree() {
    Tree *next = this;
    for (unsigned i = currCycle; i > 0; i--) {
        //if arrived at a leaf and can't go left
        if (next->getChildren().empty()) {
            break;
        }
        next = next->getChildren()[0];
    }
    return next->getNode();
}

// -------------------------------------------------  MaxRankTree  ------------------------------------------------------------

MaxRankTree::MaxRankTree(int rootLabel) : Tree(rootLabel) {}

Tree *MaxRankTree::clone() const {
    return new MaxRankTree(*this);
}

int MaxRankTree::traceTree() {
    unsigned maxRank = 0;
    queue<Tree *> rankQ;//Is used to iterate on the nodes of the BFS tree (runs BFS on the tree)
    queue<Tree *> depthQ;//Is used to compare depths if there is a tie in maxRank
    Tree *t = this;
    rankQ.push(t);
    // -------  Rank Check  -------
    while (!rankQ.empty()) {
        t = rankQ.front();
        rankQ.pop();
        if (t->getChildren().size() > maxRank) {//t is the new maxRank
            maxRank = t->getChildren().size();
            while (!depthQ.empty()) {
                depthQ.pop();
            }
            depthQ.push(t);
        } else if (t->getChildren().size() == maxRank) {//t is tied with the other maxRank nodes
            depthQ.push(t);
        }
        for (auto i : t->getChildren()) {//For the BFS iteration on the tree
            rankQ.push(i);
        }
    }
    if (depthQ.size() == 1) {//There is only one node with maxRank
        return depthQ.front()->getNode();
    } else {
    // -------  Depth Check  -------
        vector<int> output;//Is used to compare node numbers if there is a tie in minDepth
        int minDepth = INT_MAX;
        while (!depthQ.empty()) {
            t = depthQ.front();
            depthQ.pop();
            if (t->getDepth() < minDepth) {//t is the new minDepth
                minDepth = t->getDepth();
                output.clear();
                output.push_back(t->getNode());
            } else if (t->getDepth() == minDepth) {//t is tied with the other minDepth nodes
                output.push_back(t->getNode());
            }
        }
        return *min_element(output.begin(), output.end());//returns the minimal node number with minDepth
    }
}

// -------------------------------------------------  RootTree  -----------------------------------------------------

RootTree::RootTree(int rootLabel) : Tree(rootLabel) {}

Tree *RootTree::clone() const {
    return new RootTree(*this);
}

int RootTree::traceTree() {
    return node;
}