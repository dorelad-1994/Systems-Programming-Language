#ifndef TREE_H_
#define TREE_H_

#include <vector>
#include "Session.h"

using namespace std;


class Session;

class Tree{
public:
    Tree(int rootLabel);
    Tree(const Tree& other);

    Tree(Tree&& other);

    const Tree& operator=(const Tree& other);
    const Tree& operator=(Tree&& other);
    void addChild(const Tree& child);
    static Tree* createTree(const Session& session, int rootLabel);
    virtual int traceTree()=0;
    virtual Tree* clone () const=0;

    void BFS(const Session &session);
    Tree* getChild (int node);
    int getNode () const;
    const vector<Tree*>& getChildren () const;
    int getDepth() const;
    void copy(const Tree& other);
    void clearChildren();

    virtual ~Tree();

protected:
    int node;
    vector<Tree*> children;
    int depth;
//    vector<int> ranks;
//    vector<int> depths;
};

class CycleTree: public Tree{
public:
    CycleTree(int rootLabel, int currCycle);
//    CycleTree(const CycleTree& other);
//    CycleTree(CycleTree&& other);
//
//    const CycleTree& operator=(CycleTree&& other);
    virtual int traceTree();
    virtual Tree* clone() const;
    int getCurrCycle();

//    virtual ~CycleTree();

private:
    int currCycle;
};

class MaxRankTree: public Tree{
public:
    MaxRankTree(int rootLabel);
//    MaxRankTree(const MaxRankTree& other);
//    MaxRankTree(MaxRankTree&& other);
//
//    const MaxRankTree& operator=(MaxRankTree&& other);
    virtual int traceTree();
    virtual Tree* clone() const;

//    virtual ~MaxRankTree();
};

class RootTree: public Tree{
public:
    RootTree(int rootLabel);
//    RootTree(const RootTree& other);
//    RootTree(RootTree&& other);
//
//    const RootTree& operator=(RootTree&& other);
    virtual int traceTree();
    virtual Tree* clone() const;

//    virtual ~RootTree();
};

#endif
