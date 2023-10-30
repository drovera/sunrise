// Search by  genetic algorithm

#include <iostream>
#include <algorithm>
#include <vector>
using namespace std;
#include "genetic.h"

// item containing the best score, the pseudo-chomosome and the max score group
// sort key best score

Item::Item(double score, int size, int group) {
	this->score = score;
	this->size = size;
	selct = new bool[size];
	this->group = group;
}

Item::Item(double score, int size, bool* selct, int group) {
	this->score = score;
	this->size = size;
	this->selct = selct;
	this->group = group;
}

bool Item::operator<(const Item& item)const {
	return (score < item.score);
}

void Item::copy_to(Item& item) {
	item.score = score;
	copy(selct, selct + size, item.selct);
	item.group = group;
}

// genetic algorithm


genetic::genetic(net_c& net, param& p) {
	this->net = net;
	this->p = p;
}

Item genetic::get_item(int index) {
	set<Item>::iterator it = items.begin();
	for (int p = 0; p < index; p++) it++; 
	return *it;
}

void genetic::cross_over_mut(int i1, int i2, bool* chld1, bool* chld2, int cut){
	// cross over and mutations
	Item item1 = get_item(i1);
	Item item2 = get_item(i2);
	copy(item1.selct, item1.selct + cut, chld1);
	copy(item2.selct + cut, item2.selct + net.nn, chld1 + cut);
	copy(item2.selct, item2.selct + cut, chld2);
	copy(item1.selct + cut, item1.selct + net.nn, chld2 + cut);
	for (int m = 0; m < p.mut; m++) {
		int im = rand() % net.first_pos;
		chld1[im] = !chld1[im];
	}
	for (int m = 0; m < p.mut; m++) {
		int im = rand() % net.first_pos;
		chld2[im] = !chld2[im];
	}
}

void genetic::add_item(Item& item) {
	// add new item if better score, do not erase if its score alread exist to avoid decreasing the number of items
	if ((*items.begin()).score < item.score) {
		items.insert(item);
		if (items.size() > p.pop) items.erase(*items.begin());
	}
}

Item genetic::search() {
	// search by genetic algo, loop by seed and loop by generation, the better results are displayed
	p.display();
	bool* chld1 = new bool[net.nn];
	bool* chld2 = new bool[net.nn];
	Item item;
	int gen = -1;
	double gen_scr;
	double all_scr = -1.0;
	Item max_gen = Item(0.0, net.nn, -1);
	Item max_all = Item(0.0, net.nn, -1);
	int i1 = -1; int i2 = -1; int cut = -1;
	for (int seed : p.seeds) {
		srand(seed);
		gen_scr = -1.0;
		items = net.best_shortest_path(p.pop);
		for (int g = 0; g < p.gen; g++) {
			i1 = rand() % p.pop;
			i2 = rand() % p.pop;
			cut = 1 + rand() % (net.first_pos);
			cross_over_mut(i1, i2, chld1, chld2, cut);
			item = net.search_max_rem_neg(chld1);
			add_item(item);
			item = net.search_max_rem_neg(chld2);
			add_item(item);
			item = *(--items.end());
			if (item.score > gen_scr) {
				gen_scr = item.score;
				gen = g;
				item.copy_to(max_gen);				
			}		
		}
		cout << "seed: " << seed << ", gen: " << gen << ", score: " << gen_scr << endl;
		if (gen_scr > all_scr) {
			all_scr = gen_scr;
			max_gen.copy_to(max_all);
		}
	}
	delete[] chld1;
	delete[] chld2;
	return max_all;
}

int genetic::search_all() {
	// achieve and display the best result
	Item max_all = search();
	cout << "\nbest result of genetic algo: " << max_all.score << endl;
	max_all = net.search_max_rem_neg(max_all.selct); // to update groups in net for save_group
	return max_all.group;
}


// for testing functions and displaying intermediary data
ostream& operator<<(ostream& os, const Item item) {
	cout << item.score << ";";
	for (int b = 0; b < item.size; b++) cout << item.selct[b];
	cout << ";" << item.group;
	return os;
}
void genetic::disp_items() {for (Item item : items) cout<< item << endl;}
void genetic::test(){}