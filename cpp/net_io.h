#ifndef NET_IO
#define NET_IO
#include<string>
#include<vector>
using namespace std;
#include "net_reduce.h"
#include "net_connect.h"

class net_io{
public:
	netw loadSIFscr(string, string);
	net_c loadSIFredu(string, string);
private:
	int add(string, vector<string>&);
	void kv_sort(double*, vector<string>&);
	void checkin(ifstream&, string);
};

class param {
public:
	string dir;
	string project;
	int step = 0;
	//bool option = false;
	vector<int> seeds;
	int pop = -1;
	int gen = -1;
	int mut = -1;	
	bool read_param(string);
	void display();
};

#endif