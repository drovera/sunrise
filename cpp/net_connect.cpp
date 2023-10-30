// functions of connectivity, function to maximize by removing useless nodes in subnetwork

#include <iostream>
#include <fstream>
#include <sstream>
#include <set>
#include <queue>
#include <map>
#include<vector>
#include<string>
using namespace std;
#include "net_connect.h"
#define group_tag '&'

// network functions needed to the genetic algorithm

net_c::net_c(vector<string>& names, set<int>* adjacents, double* scores) {
	this->names = names;
	this->adjacents = adjacents;
	this->scores = scores;
	nn = names.size();
	groups = new int[nn];
	first_pos = 0;
	while (scores[first_pos] < 0) first_pos++;
}


void net_c::feature() {
	double neg_scr = 0.0;
	double pos_scr = 0.0;
	for (int n = 0; n < first_pos; n++) neg_scr = neg_scr + scores[n];
	for (int n = first_pos; n < nn; n++) pos_scr = pos_scr + scores[n];
	cout << "number of negative nodes: " << first_pos << ", total score: " << neg_scr << endl;
	cout << "number of positive nodes: " << nn - first_pos << ", total score: " << pos_scr << endl;
}

void net_c::set_group_map(map<string, string>& group_map) {
	this->group_map = group_map;
}


void net_c::connected(bool* select) {
	// update groups in function of selected nodes
	group_nb = 0;
	for (int n = 0; n < nn; n++) groups[n] = -1;
	bool* left = new bool[nn];
	copy(select, select + nn, left);
	queue<int> que;	
	int left_nb = nn;
	while (left_nb > 0) {
		int start = nn - 1;
		while (start > -1) {
			if (left[start]) break;
			start--;
		}
		if (start == -1) break;
		que.push(start);
		left[start] = false;
		groups[start] = group_nb;
		left_nb--;
		while (!que.empty()) {
			int n = que.front();
			que.pop();
			for (int e : adjacents[n]) {
				if (left[e]) {
					que.push(e);
					groups[e] = group_nb;
					left[e] = false;
					left_nb--;
				}
			}
		}
		group_nb++;
	}
	delete[] left;
}

void net_c::rem_neg_dgr1(int group) {
	//remove recursively negative nodes of degree 1
	// update groups
	double scr = 0.0;
	//int iter = 0;
	bool remove = true;
	while (remove) {
		remove = false;
		for (int ng = 0; ng < first_pos; ng++) {
			if (groups[ng] == group) {
				int count = 0;
				for (int e : adjacents[ng]) if (groups[e] == group) count++;
				if (count == 1) {
					groups[ng] = -1;
					scr = scr + scores[ng];
					remove = true;
				}
			}
		}
	}
}

void net_c::del_neg_keep_pos(int group) {
	// remove negative nodes degree > 1 (degree = 1 already removed) without changing the number of positive explored by increasing scores
	// update groups
	int pos_nb = 0;
	int start = nn - 1;
	for (int pos = first_pos; pos < nn; pos++) if (groups[pos] == group) {
		pos_nb++;
		start = pos;
	}
	queue<int> que;
	bool* left = new bool[nn];
	for (int rem_neg = 0; rem_neg < first_pos; rem_neg++) {
		if (groups[rem_neg] == group) {
			int count_pos = 1;
			for (int n = 0; n < nn; n++) if (groups[n] == group) left[n] = true; else left[n] = false;
			left[rem_neg] = false;
			que.push(start);
			left[start] = false;
			while (!que.empty()) {
				int n = que.front();
				que.pop();
				for (int e : adjacents[n]) {
					if (left[e]) {
						que.push(e);
						left[e] = false;
						if (e >= first_pos) count_pos++;
					}
				}
			}
			if (count_pos == pos_nb) {
				groups[rem_neg] = -1;
			}
		}
	}
	delete[] left;
}


int net_c::search_max_group(bool* select){
	// search group having maximal score
	connected(select);	
	double* g_scores = new double[group_nb];
	for (int g = 0; g < group_nb; g++) g_scores[g] = 0.0;
	for (int n = 0; n < nn; n++) if (groups[n] > -1) g_scores[groups[n]] = g_scores[groups[n]] + scores[n];
	double max_scr = scores[0];
	int max_group = 0;
	for (int g = 0; g < group_nb; g++) {
		if (g_scores[g] > max_scr) {
			max_scr = g_scores[g];
			max_group = g;
		}
	}
	delete[] g_scores;
	return max_group;
}


Item net_c::search_max_rem_neg(bool* select) {
	// search group having maximal score by removing negative nodes of degree 1 and useless negative nodes
	int group = search_max_group(select); 
	rem_neg_dgr1(group);
	del_neg_keep_pos(group);
	double scr = 0;
	for (int n = 0; n < nn; n++) if (groups[n] == group) scr = scr + scores[n];
	return Item(scr, nn, select, group);
}

void net_c::save_group(int group, string rMaxFile, string maxFile){
	// save the names of max score groups (reduced and not reduced) in files used to select nodes in Cytoscape
	ofstream out(rMaxFile);
	for (int n = 0; n < nn; n++) {
		if (groups[n] == group) out << names[n] << endl;
	}
	out.close();
	out.open(maxFile);
	for (int n = 0; n < nn; n++) {
		if (groups[n] == group) {
			if (names[n].front() == group_tag) {
				istringstream iss(group_map[names[n]]);
				string token;
				while (getline(iss, token, ';')) out << token << endl;
			}
			else out <<  names[n] << endl;
		}
	}
	out.close();
	cout << "nodes of max score saved in 2 files" << endl;
	cout << rMaxFile << " for reduced" << endl;
	cout << maxFile << " for initial network" << endl;
}


set<Item> net_c::best_shortest_path(int pop) {
	// to start genetic search build the list of items where there are the max score shortest paths and all positive nodes 
	double* dist = new double[nn];
	int* path_prev = new int[nn];
	double new_dist = 0.0;
	set<pair<double, int>> prior_que;
	set <pair<double, set<int>>> scr_path;
	for (int src = first_pos; src < nn; src++) {
		for (int i = 0; i < nn; i++) {
			dist[i] = numeric_limits<double>::max();
			path_prev[i] = src;
		}
		prior_que.insert(make_pair(0.0, src));
		dist[src] = 0.0;
		while (!prior_que.empty()) {
			int v = prior_que.begin()->second;
			prior_que.erase(*prior_que.begin());
			for (int w : adjacents[v]) {
				if (w >= first_pos) {
					new_dist = dist[v];
				}
				else new_dist = dist[v] - scores[w];
				if (dist[w] > new_dist) {
					dist[w] = new_dist;
					//cout << v << "-" << w <<  ":" << - new_dist << endl;
					path_prev[w] = v;
					prior_que.insert(make_pair(dist[w], w));
				}
			}
		}
		prior_que.clear();
		double scr = 0.0;
		for (int tgt = first_pos; (tgt < nn) && (src != tgt); tgt++) {
			set<int> path;
			int n = tgt;
			path.insert(n);
			scr = scores[n];
			while (n != src) {
				n = path_prev[n];
				path.insert(n);
				scr = scr + scores[n];
			}
			scr_path.insert(make_pair(scr, path));
		}
	}
	set<Item> items;
	set <pair<double, set<int>>>::iterator it = scr_path.end();
	for (int i = pop - 1; i > -1; i--) {
		it--;
		Item item = Item(it->first, nn, 0);
		for (int b = 0; b < first_pos; b++) item.selct[b] = false;
		for (int b = first_pos; b < nn; b++) item.selct[b] = true;
		for (int n : it->second) item.selct[n] = true;
		items.insert(item);
	}
	delete[] dist;
	delete[] path_prev;
	return items;
}

// *******************************
//  for testing functions and displaying intermediary data
void net_c::display_stru() {
	cout << "structure:" << endl;
	for (int n = 0; n < nn; ++n) {
		cout << n << "\t" << names[n] << '(' << scores[n] << ')';
		for (int edge : adjacents[n])
			cout << "\t" << names[edge];
		cout << endl;
	}
	cout << "first positive score node:" << endl;
	cout << first_pos << " - " << names[first_pos] << " : " << scores[first_pos] << endl;
	for (auto it = group_map.cbegin(); it != group_map.cend(); ++it) cout << it->first << " " << it->second << "\n";
}


void net_c::display_group(bool * select, bool groups_only, double* g_scores) {
	cout << group_nb << " groups" << endl;
	if (!groups_only) for (int n = 0; n < nn; n++) cout << names[n] << ";" << n << ";" << select[n] << ";" << groups[n] << endl;
	for (int g = 0; g < group_nb; g++) cout << "group" << g << "=" << g_scores[g] << endl;
}

void net_c::test() {

}