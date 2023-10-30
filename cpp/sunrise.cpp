// daniel.rovera@gmail.com Institut Curie - Mines Paris Tech
// read parameter and execute main

#include <iostream>
using namespace std;
#include "net_io.h"
#include "net_reduce.h"
#include "genetic.h"
#include <chrono>
using namespace std::chrono;

int main(int argc, char** argv){
	auto start = high_resolution_clock::now();
	param p;
	if (!(argc == 2)){
		cout << "missing name of parameter file" << endl;	
		return -1;
	}
	if(!p.read_param(argv[1])) {
			cout << "incorrect name of parameter file";
			return -1;
	}
	if(p.step == 0 || p.step == 1){
		cout << "step 1" << endl;
		netw net1 = net_io().loadSIFscr(p.dir + p.project + ".sif", p.dir + p.project + ".txt");
		net1.save_groups(p.dir + p.project + "-r.sif", p.dir + p.project + "-a.txt");
		cout << endl;
	}
	if (p.step == 0 || p.step == 2){
		cout << "step 2" << endl;
		net_c net2 = net_io().loadSIFredu(p.dir + p.project + "-r.sif", p.dir + p.project + "-a.txt");
		genetic gen = genetic(net2, p);
		int group = gen.search_all();
		//net2.save_group(group, p.dir + p.project + "-p.txt", p.dir + p.project + "-o.txt");
	}
	auto stop = high_resolution_clock::now();
	auto duration = duration_cast<seconds>(stop - start);
	cout << "\nduration (seconds): " << duration.count() << endl;
}