#ifndef NET_GENETIC
#define NET_GENETIC
#include<vector>
using namespace std;
#include "net_io.h"
#include "net_connect.h"

class genetic {
public:
	genetic(net_c&, param&);
	int search_all();
private:
	param p;
	net_c net;
	set<Item> items;
	Item get_item(int);
	void cross_over_mut(int, int, bool*, bool*, int);
	void add_item(Item&);
	void copy_item(Item&, Item&);
	Item search();
public: 
	void disp_item(Item);
	void disp_items();
	void test();
};

#endif
