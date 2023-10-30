#ifndef NET_CONNECT
#define NET_CONNECT
#include <set>
#include<vector>
#include<map>
#include<string>
using namespace std;

class Item {
public:
	Item() {};
	Item(double, int, int);
	Item(double, int, bool*, int);
	double score=0.0;
	int size = -1;
	bool* selct = NULL;
	int group = -1;	
	bool operator<(const Item&)const;
	void copy_to(Item&);
};
ostream& operator<<(ostream&, const Item);

class net_c {
public:
	int nn = 0;
	int first_pos = 0;
	net_c() {}
	net_c(vector<string>&, set<int>*, double*);
	set<Item> best_shortest_path(int pop);
	void set_group_map(map<string, string>&);
	void save_group(int, string, string);
	Item search_max_rem_neg(bool*);
	void feature();
private:	
	vector<string> names;
	map<string, string> group_map;
	set<int>* adjacents = NULL;
	double* scores = NULL;	
	int group_nb = 0;
	int* groups = NULL;
	void connected(bool*);
	int search_max_group(bool*);
	void rem_neg_dgr1(int);
	void del_neg_keep_pos(int);
public:
	void test();	
	void display_stru();
	void display_group(bool*, bool, double*);
};

#endif
