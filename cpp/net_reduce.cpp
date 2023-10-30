// reduce the network

#include <iostream>
#include <fstream>
#include<vector>
#include<set>
#include<string>
#include<queue>
#include<map>
using namespace std;
#include "net_reduce.h"
#define group_tag "&"

netw::netw(vector<string>& names, set<Edge>* adjacents, double* scores) {
    this->names = names;
    this->adjacents = adjacents;
    this->scores = scores;
    nn = names.size();
    first_pos = 0;
    while (scores[first_pos] < 0) first_pos++;
}


void netw::feature() {
    double neg_scr = 0.0;
    double pos_scr = 0.0;
    for (int n = 0; n < first_pos; n++) neg_scr = neg_scr + scores[n];
    for (int n = first_pos; n < nn; n++) pos_scr = pos_scr + scores[n];
    cout << "number of negative nodes: " << first_pos << ", total score: " << neg_scr << endl;
    cout << "number of positive nodes: " << nn - first_pos << ", total score: " << pos_scr << endl;
}

void netw::group_pos() {
    // number groups of connected positive nodes, length being the opposite of negative score of the end
    group_nb = 0;
    groups = new int[nn];
    for (int n = 0; n < nn; n++) groups[n] = -1;
    set<int> left;
    for (int n = first_pos; n < nn; n++) left.insert(n);
    queue<int> que;
    while (!left.empty()) {
        int start = *(left.begin());        
        que.push(start);
        groups[start] = group_nb;
        while (!que.empty()) {
            int n = que.front();
            left.erase(n);
            que.pop();
            for (Edge edge : adjacents[n]) {
                if (left.find(edge.first) != left.end()) {
                    que.push(edge.first);
                    groups[edge.first] = group_nb;
                }
            }
        }
        group_nb++;
    }
    group_nb = group_nb + first_pos;
    for (int n = 0; n < first_pos; n++) groups[n] = n;
    for (int n = first_pos; n < nn; n++) groups[n] = groups[n] + first_pos;
    g_scores = new double[group_nb];
    for (int n = 0; n < first_pos; n++) g_scores[n] = scores[n];
    for (int n = first_pos; n < group_nb; n++) g_scores[n] = 0.0;
    for (int n = first_pos; n < nn; n++) g_scores[groups[n]] = g_scores[groups[n]] + scores[n];
    g_adjacent = new set<Edge>[group_nb];
    double scr = 0.0;
    for (int n = 0; n < nn; n++) {
        for (Edge edge : adjacents[n]) {
            if (g_scores[groups[edge.first]] < 0.0) scr = - g_scores[groups[edge.first]];
            else scr = 0.0;
            if(groups[n] != groups[edge.first]) g_adjacent[groups[n]].insert(Edge(groups[edge.first], scr));
        }
    }
}

set<int> netw::only_shortest_paths() {
    // create a set containing nodes included in paths between positive group
    set<int> in_paths;
    map<int, map<int, set<int> > > paths;
    double* dist = new double[group_nb];
    int* path_prev = new int[group_nb];
    for (int src = first_pos; src < group_nb; src++) {
        for (int tgt = src + 1; tgt < group_nb; tgt++) {
            priority_queue<pair<double, int>> prior_que;
            for (int i = 0; i < group_nb; i++) dist[i] = numeric_limits<double>::max();
            prior_que.push(make_pair(0.0, src));
            int n2 = src;
            dist[src] = 0.0;
            while (!prior_que.empty()) {
                int n1 = prior_que.top().second;
                prior_que.pop();
                for (Edge edge : g_adjacent[n1]) {
                    n2 = edge.first;
                    if (dist[n2] > dist[n1] + edge.second) {
                        dist[n2] = dist[n1] + edge.second;
                        path_prev[n2] = n1;
                        if (n2 == tgt) break;
                        prior_que.push(make_pair(-dist[n2], n2));
                    }
                }
                if (n2 == tgt) break;
            }
            int n = path_prev[tgt];
            while (n != src) {
                in_paths.insert(n);
                n = path_prev[n];
            }
        }
    }
    delete[] dist;
    delete[] path_prev;
    return in_paths;
}

void netw::reducing() {
    // reduce the network to groups of positive nodes linking by negative nodes in shortest paths
    cout << "initial node nb: " << nn << endl;
    group_pos();
    cout << "group nb after grouping positive nodes: " << group_nb << endl;
    set<int> in_paths = only_shortest_paths();
    for (int n = first_pos; n < nn; n++) in_paths.insert(groups[n]);
    for (int i = 0; i < first_pos; i++)  if (in_paths.find(groups[i]) == in_paths.end()) groups[i] = -1;
    map<int, int> renum;
    group_nb = 0;
    for (int gn : in_paths) {
        renum[gn] = group_nb;
        group_nb++;
    }
    cout << "group nb after shortest paths only : " << group_nb << endl;
    for (int i = 0; i < nn; i++) {
        if (groups[i] != -1) groups[i] = renum[groups[i]];
    }
    for (int g = 0; g < group_nb; g++) {
        g_scores[g] = 0.0;
        g_adjacent[g].clear();
    }
    for (int n = 0; n < nn; n++) if (groups[n] > -1) g_scores[groups[n]] = g_scores[groups[n]] + scores[n];
    for (int n = 0; n < nn; n++) {       
        for (Edge edge : adjacents[n]){
            if((groups[n] > -1) &&(groups[edge.first] > -1)&&(groups[n] != groups[edge.first])) g_adjacent[groups[n]].insert(Edge(groups[edge.first], 1));
        }
    }
}

void netw::save_groups(string sifFile, string attrFile) {
    // save node and group scores and content of groups as attributes
    const string t = string("\t");
    reducing();
    vector<string> g_names(group_nb);
    vector<string> in_groups(group_nb);
    for (int g = 0; g < group_nb; g++) in_groups[g] = "";
    for (int n = 0; n < first_pos; n++) {
        if (groups[n] != -1) {
            g_names[groups[n]] = names[n];
            in_groups[groups[n]] = names[n];
        }
    }
    for (int n = first_pos; n < nn; n++){
        g_names[groups[n]] = names[n];
        in_groups[groups[n]] = in_groups[groups[n]] + names[n] + ";";
    }
    for (int g = groups[first_pos]; g < group_nb; g++) {
        in_groups[g].erase(size(in_groups[g]) - 1, 1);
        if(in_groups[g].find(";") != -1)  g_names[g] = group_tag + g_names[g];

    }
    ofstream out(sifFile);
    for (int src = 0; src < group_nb; src++) {
        for (Edge tgt : g_adjacent[src]) if (tgt.first > src) out << g_names[src] + t + "pp" + t + g_names[tgt.first] + "\n";
    }
    out.close();
    cout << "Compact SIF file is written: " << sifFile << endl;
    out.open(attrFile, ios::out);
    out << "shared name\tscore\tname_in_group\n";
    for (int g = 0; g < group_nb; g++) out << g_names[g] + t + to_string(g_scores[g]) + t + in_groups[g] + "\n";
    out.close();
    cout << "Compact attribute file is written: " << attrFile << endl;
}

// for checking

void netw::disp_stru_name(){
	for (int n = 0; n < nn; ++n) {
		cout << n << ";" << names[n];
        if (scores != NULL) cout << '(' << scores[n] << ')';
		for (Edge edge : adjacents[n])
			cout << ";" << names[edge.first];
		cout << endl;
	}
    if (scores != NULL) {
        cout << "first positive score node:" << endl;
        cout << first_pos << " - " << names[first_pos] << " : " << scores[first_pos] << endl;
    }
}

void netw::disp_g_stru(){
    for (int g = 0; g < group_nb; g++) {
        cout << g;
        for (Edge e : g_adjacent[g]) cout<<";"<<e.first << ":" << e.second;
        cout << endl;
    }
}


void netw::test() {
    disp_stru_name();
}
