// read intial file and reduced file
// read the parameter file in command line

#include <iostream>
#include <fstream>
#include <sstream>
#include <set>
#include <map>
using namespace std;
#include "net_io.h"
#define group_tag '&'


netw net_io::loadSIFscr(string sifFile, string scrFile) {
	// load network, adjacency as set of edges containing end index and length, nodes sorted by scores
	set<string> name_set;
	ifstream in_sif(sifFile);
	checkin(in_sif, sifFile);
	string line;
	while (getline(in_sif, line)) {
		int pos1 = line.find_first_of("\t");
		if (pos1 == -1) {
			cout << line << " isolated node" << endl;
			continue;
		}
		name_set.insert(line.substr(0, pos1));
		int pos2 = line.find_last_of("\t");
		if (pos2 == -1) {
			cout << "abnormal line, break" << endl;
			break;
		}
		name_set.insert(line.substr(pos2 + 1, line.length() - pos2));
	}
	in_sif.close();
	vector<string> names;
	double* scores = new double[name_set.size()];
	for (int i = 0; i < name_set.size(); i++) scores[i] = 0.0;
	ifstream in_scr(scrFile);
	checkin(in_scr, scrFile);
	getline(in_scr, line);//title line
	int is = 0;
	while (getline(in_scr, line)) {
		int pos = line.find_first_of("\t");
		if (pos == -1) {
			cout << "abnormal line, break" << endl;
			break;
		}
		string name = line.substr(0, pos);
		names.push_back(name);
		name_set.erase(name);
		string score = line.substr(pos + 1, line.length() - pos);
		scores[is] = stod(score);
		is++;
	}
	cout << "scores loaded from " << scrFile << endl;
	cout << "without score nodes, score = 0 : " << endl;
	for (string name : name_set) {
		cout << name << endl;
		names.push_back(name);
	}
	kv_sort(scores, names);
	vector<int> srcs;
	vector<int> tgts;
	vector<string>::iterator it;
	in_sif.open(sifFile, ifstream::in);
	while (getline(in_sif, line)) {
		int pos1 = line.find_first_of("\t");
		if (pos1 == -1) {
			cout << line << " isolated node" << endl;
			continue;
		}
		it = find(names.begin(), names.end(), line.substr(0, pos1));
		srcs.push_back(it - names.begin());
		int pos2 = line.find_last_of("\t");
		if (pos2 == -1) {
			cout << "abnormal line, break" << endl;
			break;
		}
		it = find(names.begin(), names.end(), line.substr(pos2 + 1, line.length() - pos2));
		tgts.push_back(it - names.begin());
	}
	cout << "network loaded from " << sifFile << endl;
	set<Edge>* adjacents = new set<Edge>[names.size()];
	for (int i = 0; i < srcs.size(); i++) {
		adjacents[srcs[i]].insert(Edge(tgts[i], 1.0));
		adjacents[tgts[i]].insert(Edge(srcs[i], 1.0));
	}	
	netw net = netw(names, adjacents, scores);
	net.feature();
	return net;
}

net_c net_io::loadSIFredu(string sifFile, string attrFile) {
	// load reduced network, adjacency as set of node index, nodes sorted by scores
	set<string> name_set;
	ifstream in_sif(sifFile);
	checkin(in_sif, sifFile);
	string line;
	while (getline(in_sif, line)) {
		int pos1 = line.find_first_of("\t");
		name_set.insert(line.substr(0, pos1));
		int pos2 = line.find_last_of("\t");
		name_set.insert(line.substr(pos2 + 1, line.length() - pos2));
	}
	in_sif.close();
	vector<string> names;
	double* scores = new double[name_set.size()];
	for (int i = 0; i < name_set.size(); i++) scores[i] = 0.0;
	ifstream in_attr(attrFile);
	checkin(in_attr, attrFile);
	map<string, string> group_map;
	getline(in_attr, line);//title line
	int is = 0;
	while (getline(in_attr, line)) {
		int pos = line.find_first_of("\t");
		string name = line.substr(0, pos);
		names.push_back(name);
		string score = line.substr(pos + 1, line.length() - pos);
		scores[is] = stod(score);
		if (name.front() == group_tag) {
			pos = line.find_last_of("\t");
			group_map[name] = line.substr(pos + 1, line.length() - pos);
		}
		is++;
	}
	cout << "scores loaded from " << attrFile << endl;
	kv_sort(scores, names);
	vector<int> srcs;
	vector<int> tgts;
	vector<string>::iterator it;
	in_sif.open(sifFile, ifstream::in);
	while (getline(in_sif, line)) {
		int pos1 = line.find_first_of("\t");
		it = find(names.begin(), names.end(), line.substr(0, pos1));
		srcs.push_back(it - names.begin());
		int pos2 = line.find_last_of("\t");
		it = find(names.begin(), names.end(), line.substr(pos2 + 1, line.length() - pos2));
		tgts.push_back(it - names.begin());
	}
	cout << "network loaded from " << sifFile << endl;
	set<int>* adjacents = new set<int>[names.size()];
	for (int i = 0; i < srcs.size(); i++) {
		adjacents[srcs[i]].insert(tgts[i]);
		adjacents[tgts[i]].insert(srcs[i]);
	}
	net_c net = net_c(names, adjacents, scores);
	net.set_group_map(group_map);
	net.feature();
	return net;
}

int net_io::add(string name, vector<string>& names) {
	// index of a node from the name
	vector<string>::iterator it = find(names.begin(), names.end(), name);
	if (it != names.end()) return it - names.begin();
	else {
		names.push_back(name);
		return names.size() - 1;
	}
}

void net_io::kv_sort(double* keys, vector<string>& vals) {
	// used for sorting names by scores
	double t_key; string t_val;
	int i, j;
	for (i = 1; i < vals.size(); i++) {
		t_key = keys[i];
		t_val = vals[i];
		j = i - 1;
		while (j >= 0 && keys[j] > t_key) {
			keys[j + 1] = keys[j];
			vals[j + 1] = vals[j];
			j = j - 1;
		}
		keys[j + 1] = t_key;
		vals[j + 1] = t_val;
	}
}

void net_io::checkin(ifstream& inf, string file) {
	if (!inf) {
		cerr << file << " unknown" << endl;
		exit(-1);
	}
}

bool param::read_param(string file) {
	// read parameters from parameter file in command line
	ifstream in_(file);
	map<string, string> params;
	if (in_.is_open()) {
		string line;
		while (getline(in_, line))
		{
			line.erase(remove_if(line.begin(), line.end(), isspace), line.end());
			if (line.empty() || line[0] == '#') continue; // # is comment
			size_t delimPos = line.find("=");
			params[line.substr(0, delimPos)] = line.substr(delimPos + 1);
		}
	}
	else {
		cerr << "Couldn't open param file " << file << endl;
		return false;
	}
	try {

		dir = params["dir"];
		project = params["project"];
		step = stoi(params["step"]);
		//option = (bool)stoi(params["option"]);
		istringstream iss(params["seeds"]);
		string token;
		while (getline(iss, token, ';')) seeds.push_back(stoi(token));
		pop = stoi(params["population"]);
		gen = stoi(params["generation"]);
		mut = stoi(params["mutation"]);		
	}
	catch (exception e) {
		cerr << "Couldn't convert data\n";
		return false;
	}
	return true;
}

void param::display() {
	cout << "population: " << pop << ", generation: " << gen << ", mutation: " << mut << endl;
}