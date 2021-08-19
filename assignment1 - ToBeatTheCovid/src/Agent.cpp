//
// Created by spl211 on 05/11/2020.
//
#include "Tree.h"
# include "Agent.h"
# include <queue>
#include <iostream>

using namespace std;

Agent::Agent() : canContinue(false){}

bool Agent::getCanContinue() const {
    return canContinue;
}

Agent::~Agent() {}

// Virus:

Virus::Virus(int nodeInd) : Agent(), nodeInd(nodeInd), lastInfected(-1) {
    canContinue = true;
}

void Virus::act(Session &session) {
    if (canContinue) {
        const Graph& g = session.getGraph();
        if (!g.isInfected(nodeInd)) {
            session.enqueueInfected(nodeInd);
        }
        unsigned i = lastInfected + 1;
        while (i < g.getEdges().size()) {
            if (g.areNeighbours(nodeInd, i) && !session.hasVirus(i)) {
                lastInfected = i;
                Agent *v = new Virus(i);
                session.addAgent(*v);
                session.setNodeHasVirus(i);
                if (v) {
                    delete v;
                    v = nullptr;
                }
                break;
            }
           i++;
        }
        canContinue = !(i == g.getEdges().size());// Virus has no more non virus free neighbours and can't continue
    }

}

Agent * Virus::clone() const {
    return new Virus(*this);
}

// Contact Tracer

ContactTracer::ContactTracer() : Agent() {}

void ContactTracer::act(Session &session) {
    if (!session.infectedQueueIsEmpty()) {
        Tree *t = Tree::createTree(session, session.dequeueInfected());
        t->BFS(session);
        int i = t->traceTree();
        session.disconnect(i);
        delete t;
    }
}

Agent * ContactTracer::clone() const {
    return new ContactTracer(*this);
}
