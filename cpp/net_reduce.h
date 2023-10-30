#ifndef NET_REDUCE
#define NET_REDUCE
#include <set>
#include<vector>
#include<string>
using namespace std;

typedef pair<int, double> Edge; // first: node, second: distance

class netw {
public:
	netw(vector<string>&, set<Edge>*, double*);
	void feature();
	void save_groups(string, string);
private:
	int nn = 0;
	vector<string> names;
	set<Edge>* adjacents = NULL;
	double* scores = NULL;
	int first_pos = 0;
	int group_nb = 0;
	int* groups = NULL;
	set<Edge>* g_adjacent = NULL;
	double* g_scores = NULL;
	void group_pos();
	set<int> only_shortest_paths();
	void reducing();
public: // for checking
	void disp_stru_name();
	void disp_g_stru();
	void test();
};
#endif
